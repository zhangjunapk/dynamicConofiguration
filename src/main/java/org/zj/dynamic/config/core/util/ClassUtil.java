package org.zj.dynamic.config.core.util;

import org.zj.dynamic.config.core.common.MagicValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.core.util
 * @Author: ZhangJun
 * @CreateTime: 2018/12/30
 * @Description: ${Description}
 */
public class ClassUtil {
    /**
     * 获得指定包名下的所有类
     * @param packageName
     * @return
     */
    public static List<Class> getClassesByPackageName(String packageName){
        String packagePath = getPackagePath(packageName);
        return getClassesByPath(new File(packagePath));
    }

    public static List<Class> getClassesByPath(File file){
        List<Class> result=new ArrayList<Class>();
        if(!file.exists()){
            return null;
        }
        if(file.isDirectory()) {
            for (File f : file.listFiles()) {
                result.addAll(getClassesByPath(f));
            }
        }

        if(file.isFile()&&file.getAbsolutePath().endsWith(MagicValue.END_JAVA)){
            Class aClass = parseJavaFileToClass(file);
            if(aClass!=null) {
                result.add(aClass);
            }
        }

        return result;
    }

    /**
     * 把java 文件解析成class
     * @param file
     * @return
     */
    private static Class parseJavaFileToClass(File file) {
        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);
        System.out.println(getJavaPath());
        String s=absolutePath.replace(getJavaPath(),"").replace("\\",".");
        System.out.println(s);
        String className=s.substring(0,s.lastIndexOf("."));
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获得项目的绝对路径
     * @return
     */
    public static String getProjectPath(){
        return System.getProperty("user.dir");
    }

    /**
     * 获得指定包名所在的绝对路径
     * @param packageName
     * @return
     */
    public static String getPackagePath(String packageName){
        return getJavaPath()+packageName.replace(".", "\\");
    }

    /**
     * 获得java代码路径
     * @return
     */
    public static String getJavaPath(){
        return getProjectPath()+"\\src\\main\\java\\";
    }

    /**
     * 获得指定类全名的绝对路径
     * @param className
     * @return
     */
    public static String getClassPath(String className){
        return getJavaPath()+className.replace(".","\\")+".java";
    }
    public static void main(String[] args) {
        System.out.println( ClassUtil.getClassPath("org.zj.dynamic.config.core.ConfigurationListener"));
    }

}
