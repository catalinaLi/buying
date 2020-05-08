package com.crawler.xiaomi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.crawler.xiaomi.annotation.Async;
import com.crawler.xiaomi.annotation.Controller;
import com.crawler.xiaomi.annotation.Resource;
import com.crawler.xiaomi.annotation.Singleton;
import com.crawler.xiaomi.db.GoodsInfoStorage;
import com.crawler.xiaomi.db.LogStorage;
import com.crawler.xiaomi.manage.Config;
import com.crawler.xiaomi.manage.FilePathManage;
import com.crawler.xiaomi.manage.StatusManage;
import com.crawler.xiaomi.pojo.GoodsConfig;
import com.crawler.xiaomi.service.LogService;
import com.crawler.xiaomi.service.XiaoMiService;
import com.crawler.xiaomi.utils.FileUtil;

import java.util.Map;

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

    @Resource
    private LogService logService;

    @Singleton
    @Async
    public void init() {
        logService.readLogs();
        String string = FileUtil.readFileToString(FilePathManage.goodsInfoDb);
        if(string.length()!=0){
            GoodsInfoStorage.putAll(JSON.parseObject(string,new TypeReference<Map<String,GoodsConfig>>(){}));
        }
    }

    public void start(){

        StatusManage.isLogin = false;
        StatusManage.submitCount.set(0);
        StatusManage.cartCount.set(0);
        Config.goodsInfo.getBuyUrls().clear();

        FileUtil.checkPath(FilePathManage.configPath);
        FileUtil.writeToFile(JSON.toJSONString(Config.goodsInfo), FilePathManage.goodsInfoConfig);
        xiaoMiService.start();
    }

    public String loadLog() {
        return LogStorage.getLog();
    }

    public void stop(String msg) {
        xiaoMiService.stop(msg);
    }

    public String searchGoods(String name) {
        return xiaoMiService.searchGoods(name);
    }
}
