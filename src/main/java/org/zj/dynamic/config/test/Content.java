package org.zj.dynamic.config.test;

import org.zj.dynamic.config.core.ConfigurationListener;
import org.zj.dynamic.config.core.annotation.DataSource;
import org.zj.dynamic.config.core.annotation.NeedInflateConfigurationPackage;
import org.zj.dynamic.config.core.enums.DBType;
import org.zj.dynamic.config.core.util.ClassUtil;

import java.util.Random;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.test
 * @Author: ZhangJun
 * @CreateTime: 2018/12/30
 * @Description: ${Description}
 */
@NeedInflateConfigurationPackage("org.zj.dynamic.config")
@DataSource(userName = "root",password = "",url="jdbc:mysql://localhost:3306/test_dbutil?serverTimezone=UTC",
        driverClassName = "com.mysql.jdbc.Driver",dbType = DBType.MYSQL,tableName = "test_config")

/**
 * url= jdbc:mysql://localhost:3306/test_dbutil?serverTimezone=UTC
 * driver-class-name= com.mysql.jdbc.Driver
 * username= root
 * password=
 */
public class Content {
    public static void main(String[] args) {

        new Thread(() -> {
            System.out.println("-------------");
            doTest();
        }).run();


    }

    private static void doTest() {
        NeedInflateConfigurationPackage needInflateConfigurationPackage = Content.class.getAnnotation(NeedInflateConfigurationPackage.class);
        DataSource annotation = Content.class.getAnnotation(DataSource.class);
        new ConfigurationListener(needInflateConfigurationPackage,annotation).listen();
    }

}
