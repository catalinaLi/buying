package com.crawler.xiaomi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lllx
 * @Description: 重试,不限次数,成功为止
 *  方法注解，可以和@Singleton 组合使用
 * @Date: Created on 12:28 2020/4/8
 * @Modefied by:
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry2 {

    String success();

    long interval() default 0;

}