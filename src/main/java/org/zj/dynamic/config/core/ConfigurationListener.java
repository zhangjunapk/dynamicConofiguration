package org.zj.dynamic.config.core;

import com.alibaba.druid.pool.DruidDataSource;
import org.zj.dynamic.config.core.annotation.DataSource;
import org.zj.dynamic.config.core.annotation.NameSpace;
import org.zj.dynamic.config.core.annotation.NeedInflateConfigurationPackage;
import org.zj.dynamic.config.core.annotation.Value;
import org.zj.dynamic.config.core.entity.KeyUpdateEntity;
import org.zj.dynamic.config.core.enums.DBType;
import org.zj.dynamic.config.core.util.ClassUtil;
import org.zj.dynamic.config.core.util.DBUtil;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.core
 * @Author: ZhangJun
 * @CreateTime: 2018/12/30
 * @Description: ${Description}
 */
public class ConfigurationListener {
    private List<Class> classes=new ArrayList<>();
    private Map<String,Class> classTableMap=new HashMap<>();
    private Map<String, List<KeyUpdateEntity>> keyUpdateMap=new HashMap<>();

    private DataSource dataSource;
    private NeedInflateConfigurationPackage needInflateConfigurationPackage;
    private DruidDataSource druidDataSource;

    private DBUtil dbUtil;
    public ConfigurationListener(NeedInflateConfigurationPackage needInflateConfigurationPackage, DataSource dataSource){
        this.needInflateConfigurationPackage=needInflateConfigurationPackage;
        this.dataSource=dataSource;
        initDBUtil();
    }

    private void initDBUtil() {
        if(dbUtil==null&&dataSource!=null){
            dbUtil=new DBUtil(dataSource.userName(),dataSource.password(),dataSource.url(),dataSource.driverClassName());
        }
    }

    /**
     * 监听配置的方法
     */
    public void listen(){
        init();
        doListen();
    }

    /**
     * 每隔一段时间就执行
     */
    private void doListen() {
        for(Map.Entry<String,Class> entry:classTableMap.entrySet()){
            checkAndReflection(entry.getKey(),entry.getValue());
        }
    }

    /**
     * 扫描这个表的上次更新时间，然后判断是否需要对这个类重新反射赋值
     * @param nameSpace
     * @param c
     */
    private void checkAndReflection(String nameSpace, Class c) {
        checkAndInit(nameSpace,c);
        checkAndUpdateValue(dataSource.tableName(),nameSpace,c);
    }

    /**
     * 这里就要检测上次的更新时间了
     * @param tableName
     * @param nameSpace
     * @param c
     */
    private void checkAndUpdateValue(String tableName, String nameSpace, Class c) {
        for(Field f:c.getDeclaredFields()){
            if(!f.isAnnotationPresent(Value.class)){
                continue;
            }

            Value value = f.getAnnotation(Value.class);
            Date lastUpdateDate = getLastUpdateDate(tableName, nameSpace, value.key());
            checkAndReflectionVal(value.key(),nameSpace,lastUpdateDate,f,c);
        }
    }

    /**
     * 检测上次更新时间和map中是否一致，如果不一致说明更新了，那就重新从mysql获得并反射注入
     * 1.整个namespace都没有反射注入过
     * 2.仅仅是当前的key没有反射注入过
     * @param key
     * @param nameSpace
     * @param lastUpdateDate
     * @param f
     * @param c
     */
    private void checkAndReflectionVal(String key, String nameSpace, Date lastUpdateDate, Field f, Class c) {

        if(!f.isAnnotationPresent(Value.class)){
            return;
        }

        //整个namespace都没有反射注入到配置类里面过
        if(keyUpdateMap.get(nameSpace)==null||keyUpdateMap.get(nameSpace).size()==0){
            String value=getValue(nameSpace,key);
            changeFieldValue(value,f);
            ArrayList<KeyUpdateEntity> list= new ArrayList<>();
            list.add(new KeyUpdateEntity(nameSpace,lastUpdateDate));
            keyUpdateMap.put(nameSpace,list);
            System.out.println("整个namsspace都没有注入过");
            return;
        }

        //接下来是里面有，但是需要更新的情况
        Value value = f.getAnnotation(Value.class);
        KeyUpdateEntity keyUpdateEntity = getKeyUpdateEntity(nameSpace, value.key());
        if(keyUpdateEntity!=null&&keyUpdateEntity.getUpdateTime()!=null&&keyUpdateEntity.getUpdateTime()==lastUpdateDate){
            System.out.println("这个和上次更改时间一致，不进行更改");
            return;
        }

        String v=getValue(nameSpace,key);
        if(v==null||"".equals(v)){
            System.out.println("从数据库读取有问题，空的");
            return;
        }
        changeFieldValue(v,f);
        List<KeyUpdateEntity> keyUpdateEntities = keyUpdateMap.get(nameSpace);
        keyUpdateEntities.add(new KeyUpdateEntity(key,lastUpdateDate));
        keyUpdateMap.put(nameSpace,keyUpdateEntities);
        System.out.println("仅仅是当前的没有注入过");
    }

    /**
     * 更改这个变量的值
     * @param value
     * @param f
     */
    private void changeFieldValue(String value, Field f) {
        f.setAccessible(true);
        try {
            f.set(null,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String getValue(String nameSpace, String key) {
        String sql=null;
        if(dataSource.dbType()== DBType.MYSQL) {
            sql = "select `value` from " + dataSource.tableName() + " where name_space='" + nameSpace + "' and `key` ='" + key + "' limit 1;";
        }
        System.out.println(sql);
        return (String) dbUtil.getResult(sql,String.class,"value");
    }

    /**
     * 检查那个类型的是否存在,不存在就挨个插入到表
     * @param nameSpace
     * @param c
     */
    private void checkAndInit( String nameSpace, Class c) {
        checkAndInsertInitValue(nameSpace,c);
    }

    private void checkAndInsertInitValue(String nameSpace,Class c) {
        for(Field f:c.getDeclaredFields()){
            if(!f.isAnnotationPresent(Value.class)){
                continue;
            }
            Value value= f.getAnnotation(Value.class);
            if(value.key()==null||"".equals(value.key())||value.init()==null||"".equals(value.init())){
                continue;
            }

            //如果数据库中有就不用再放进去了
            if(isExistKey(nameSpace,value.key())){
                System.out.println("初始化过，就不用了");
               continue;
            }

            String sql=generateInsertKeyValueSql(nameSpace,value);
            dbUtil.runSql(sql);
        }
    }

    /**
     * 生成插入键值对的sql
     * @param nameSpace
     * @param value
     * @return
     */
    private String generateInsertKeyValueSql(String nameSpace, Value value) {
        if(value==null||dataSource==null||dataSource.tableName()==null||"".equals(dataSource.tableName())){
            return null;
        }
        String sql="insert into "+dataSource.tableName()+" (`key`,`value`,`name_space`,`note`) values ('"+value.key()+"','"+value.init()+"','"+nameSpace+"','"+value.comment()+"');";
        return sql;
    }

    /**
     *
     * @param tableName
     */
    private void checkAndCreateTable(String tableName) {
        //如果表不存在就创建表
        if(!dbUtil.isExistTable(tableName,dataSource.dbType())){
            String sql=generateCreateTableSql(tableName);
            System.out.println(sql);
            dbUtil.runSql(sql);
        }
    }

    /**
     * 生成创建表的sql
     * @param tableName
     * @return
     */
    private String generateCreateTableSql(String tableName) {
        String sql="create table "+tableName+"(`key` varchar(20) comment '键',`value` varchar(20) comment '值',`name_space` varchar(500) comment '命名空间',`note` varchar(50) comment '这条记录的注释');";
        sql+="ALTER TABLE "+tableName+" ADD COLUMN `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间';";
        return sql;
    }

    /**
     * 初始化
     */
    private void init(){
        inflateClasses();
        inflateMapping();
        initData();
    }

    private void initData() {
        checkAndCreateTable(dataSource.tableName());
    }

    /**
     * 把表名和类的映射关系放到集合中
     */
    private void inflateMapping() {
        if(classes==null||classes.size()==0){
            return;
        }

        for(Class c:classes){
            if(!c.isAnnotationPresent(NameSpace.class)){
                continue;
            }
            NameSpace nameSpace= (NameSpace) c.getAnnotation(NameSpace.class);
            classTableMap.put(nameSpace.value(),c);
        }
    }

    /**
     * 扫描当前项目中的所有类
     */
    private void inflateClasses() {
        for(String packageName:needInflateConfigurationPackage.value()){
            classes.addAll(ClassUtil.getClassesByPackageName(packageName));
        }
    }

    /**
     * 获得指定namspace,指定key的上次更细时间
     * @param tableName
     * @param nameSpace
     * @param key
     * @return
     */
    private Date getLastUpdateDate(String tableName,String nameSpace,String key){
        String sql="select update_time from `"+tableName+"` where `name_space`='"+nameSpace+"' and `key`='"+key+"';";
        return (Date) dbUtil.getResult(sql,Date.class,"update_time");
    }

    /**
     * 根据命名空间和key来获得
     * @param nameSpace
     * @param key
     * @return
     */
    private KeyUpdateEntity getKeyUpdateEntity(String nameSpace,String key){
        if(keyUpdateMap.size()==0||keyUpdateMap.get(nameSpace)==null){
            return null;
        }
        List<KeyUpdateEntity> keyUpdateEntities = keyUpdateMap.get(nameSpace);
        for(KeyUpdateEntity b:keyUpdateEntities){
            if(b.getKey()!=null&&!"".equals(b.getKey())&&b.getKey().equals(key)){
                return b;
            }
        }
        return null;
    }

    /**
     * 检测这个key在数据库中是否已经存在
     * @param nameSpace
     * @param key
     * @return
     */
    private boolean isExistKey(String nameSpace,String key){
        String sql="select * from "+dataSource.tableName()+" where name_space='"+nameSpace+"' and `key`='"+key+"';";
        return dbUtil.isExistResultSet(sql);
    }
}
