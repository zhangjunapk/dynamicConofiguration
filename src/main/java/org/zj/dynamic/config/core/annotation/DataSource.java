package org.zj.dynamic.config.core.annotation;

import org.zj.dynamic.config.core.enums.DBType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.core.annotation
 * @Author: ZhangJun
 * @CreateTime: 2019/1/2
 * @Description: ${Description}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    String userName();
    String password();
    String url();
    String driverClassName();
    String tableName();
    DBType dbType();
}
