package com.crawler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 11:50 2020/4/8
 * @Modefied by:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {
    int value() default 1;//异步线程的个数
    long interval() default 0;//间隔
}
