package com.crawler.xiaomi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lllx
 * @Description: 方法单次运行
 *  方法注解，可以和所有方法注解组合使用
 * @Date: Created on 14:47 2020/4/8
 * @Modefied by:
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Singleton {

    String value() default "";
}
