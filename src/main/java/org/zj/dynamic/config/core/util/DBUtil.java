package org.zj.dynamic.config.core.util;

import com.alibaba.druid.pool.DruidDataSource;
import org.zj.dynamic.config.core.annotation.DataSource;
import org.zj.dynamic.config.core.enums.DBType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.core.util
 * @Author: ZhangJun
 * @CreateTime: 2019/1/2
 * @Description: ${Description}
 */
public class DBUtil {
    private DruidDataSource druidDataSource;
    private Connection connection;
    public DBUtil(String username,String password,String url,String driverClassName){
        druidDataSource=new DruidDataSource();
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(driverClassName);
        druidDataSource.setUrl(url);
    }

    public DBUtil(DruidDataSource druidDataSource){
        this.druidDataSource=druidDataSource;
    }

    /**
     * 根据sql和标识类型的类，表中字段名来获得结果
     * @param sql
     * @param c
     * @param fieldName
     * @return
     */
    public Object getResult(String sql,Class c,String fieldName){
        ResultSet resultSet = getResultSet(sql);
        if(resultSet==null){
            return null;
        }
        return parseResultSet(resultSet,c,fieldName);
    }

    /**
     * 把结果解析出来
     * @param resultSet
     * @param c
     * @param fieldName
     * @return
     */
    private Object parseResultSet(ResultSet resultSet, Class c, String fieldName) {

        if(resultSet==null||c==null||fieldName==null||"".equals(fieldName)){
            return null;
        }

        if(c== Date.class){
            try {
                return resultSet.getDate(fieldName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(c== String.class){
            try {
                return resultSet.getString(fieldName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Connection getConnection(){
        if(connection==null&&druidDataSource!=null){
            try {
                return druidDataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private Statement getStatement(){
        try {
            Connection connection = getConnection();
            if(connection!=null) {
                return connection.createStatement();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    private ResultSet getResultSet(String sql){
        ResultSet rawResultSet = getRawResultSet(sql);
        try {
            rawResultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rawResultSet;
    }

    /**
     * 检测表是否存在
     * @param tableName
     * @return
     */
    public boolean isExistTable( String tableName, DBType dbType){
        if(dbType==null){
           return false;
        }
        String sql = null;
        if(dbType==DBType.ORACLE){
            sql="select count(1) from user_objects where  object_name =  '"+tableName+"';";
        }
        if(dbType==DBType.MYSQL){
            sql="show tables like '"+tableName+"';";
        }
        System.out.println(sql);
        if(druidDataSource==null){
            return false;
        }

        try {
            return new DBUtil(druidDataSource).getRawResultSet(sql).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(sql==null){
            return false;
        }
        return false;
    }

    /**
     * 运行sql
     * @param sql
     */
    public void runSql(String sql){
        if(sql==null||"".equals(sql)){
            return;
        }
        try {
            getStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断执行这个sql是否有结果
     * @param sql
     * @return
     */
    public boolean isExistResultSet(String sql){
        try {
            return getResultSet(sql).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getRawResultSet(String sql){
        Statement statement = getStatement();
        if(statement==null){
            return null;
        }
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
