package com.crawler.xiaomi.controller;

import com.crawler.xiaomi.annotation.Controller;
import com.crawler.xiaomi.annotation.Resource;
import com.crawler.xiaomi.service.XiaoMiService;

/**
 * @Author: lllx
 * @Description:
 * @Date: Created on 14:29 2020/4/16
 * @Modefied by:
 */
@Controller
public class XiaoMiController {

    @Resource
    private XiaoMiService xiaoMiService;


    public String searchGoods(String name) {
        return xiaoMiService.searchGoods(name);
    }
}
