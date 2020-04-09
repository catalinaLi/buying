package com.crawler.xiaomi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lllx
 * @Description: 任务停止注解,用于停止定时任务
 *  方法注解，可以和@Async @Singleton 组合使用
 * @Date: Created on 15:04 2020/4/8
 * @Modefied by:
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Stop {
    String[] methods();
}
