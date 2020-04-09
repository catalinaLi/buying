package com.crawler.xiaomi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lllx
 * @Description: 异步执行
 *  方法注解，可以和@Singleton @Stop 组合使用
 * @Date: Created on 11:50 2020/4/8
 * @Modefied by:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {
    int value() default 1;//异步线程的个数
    long interval() default 0;//间隔
}
