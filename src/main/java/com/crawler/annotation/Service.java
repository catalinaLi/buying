package com.crawler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 10:59 2020/4/8
 * @Modefied by:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Service {
    String value() default "";//服务名称
}
