package com.crawler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 11:02 2020/4/8
 * @Modefied by:
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {
    String value() default "";
}
