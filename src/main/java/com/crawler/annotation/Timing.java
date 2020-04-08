package com.crawler.annotation;

import com.crawler.enums.TimingType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 11:52 2020/4/8
 * @Modefied by:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timing {
    TimingType TYPE();

    int initialDelay();

    int period();

    TimeUnit unit();
}
