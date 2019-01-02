package org.zj.dynamic.config.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.core.annotation
 * @Author: ZhangJun
 * @CreateTime: 2018/12/30
 * @Description: ${Description}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Value {
    String key();
    String init()default "";
    String comment()default "";
}
