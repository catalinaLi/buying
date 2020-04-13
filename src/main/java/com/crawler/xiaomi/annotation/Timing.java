package com.crawler.xiaomi.annotation;

import com.crawler.xiaomi.enums.TimingType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lllx
 * @Description: 定时任务
 * 方法注解，可以和@Singleton 组合使用
 * @Date: Created on 11:52 2020/4/8
 * @Modefied by:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timing {
    TimingType type();

    int initialDelay();

    int period();

    TimeUnit unit();
}
