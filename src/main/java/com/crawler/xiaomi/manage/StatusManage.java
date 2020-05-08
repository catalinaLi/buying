package com.crawler.xiaomi.manage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: lllx
 * @Description: 爬虫状态标志
 * @Date: Created on 15:12 2020/5/6
 * @Modefied by:
 */
public class StatusManage {

    //登录状态
    public static volatile boolean isLogin = false;

    //抢购结束标志
    public static volatile boolean isEnd = false;

    //结束消息
    public static volatile String endMsg = "";

    //提交订单的次数
    public static AtomicInteger submitCount = new AtomicInteger(0);

    //添加购物车的次数
    public static AtomicInteger cartCount = new AtomicInteger(0);
}
