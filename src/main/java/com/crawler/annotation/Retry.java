package com.crawler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重试
 * 方法注解，可以和@Singleton 组合使用
 * @Author: lllx
 * @Description:
 * @Date: Created on 12:27 2020/4/8
 * @Modefied by:
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

    int count() default 0;

    Class<?>[] retException();

}