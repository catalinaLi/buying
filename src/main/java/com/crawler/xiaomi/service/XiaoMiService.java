package com.crawler.xiaomi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crawler.xiaomi.annotation.Resource;
import com.crawler.xiaomi.annotation.Service;
import com.crawler.xiaomi.db.GoodsInfoStorage;
import com.crawler.xiaomi.manage.Config;
import com.crawler.xiaomi.manage.FilePathManage;
import com.crawler.xiaomi.pojo.GoodsConfig;
import com.crawler.xiaomi.utils.FileUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @Author: lllx
 * @Description: 小米抢购服务
 * @Date: Created on 16:14 2020/4/13
 * @Modefied by:
 */
@Service
public class XiaoMiService {

    private static Logger logger = LoggerFactory.getLogger(XiaoMiService.class);

    @Resource
    private HttpService httpService;


    /**
     * 商品搜索
     * @param name
     * @return
     */
    public String searchGoods(String name) {
        if (name.startsWith("http")) {
            if(!name.startsWith("https://item.mi.com/product/")){
                return "(不支持该链接)";
            }
            Config.goodsConfig = queryGoodsInfo(name);
            if(Config.goodsConfig==null){
                return "(未找到该商品)";
            }
            GoodsInfoStorage.put(Config.goodsConfig.getName(), Config.goodsConfig);
            FileUtil.writeToFile(JSON.toJSONString(GoodsInfoStorage.getAll()), FilePathManage.goodsInfoDb);
            return null;
        }
        Config.goodsConfig = GoodsInfoStorage.get(name);
        return Config.goodsConfig==null?"(商品暂未收录，试试商品地址)":null;
    }

    /**
     * 查询商品详情
     * @param url
     * @return
     */
    private GoodsConfig queryGoodsInfo(String url) {
        try{
            String goodsId = url.substring(url.indexOf("product")+8,url.indexOf("html")-1);
            String goodInfoUrl = "https://order.mi.com/product/get?jsonpcallback=proget2callback&product_id="
                    +goodsId+"&_="+System.currentTimeMillis();
            String ret = httpService.getXiaomi(goodInfoUrl, url);
            return parseGoodsInfo(ret,url);
        }catch(Exception e ){
            logger.error("queryGoodsInfo by:{} err",url,e);
            return null;
        }
    }

    /**
     * 解析商品详情
     * @param ret
     * @param url
     * @return
     */
    private GoodsConfig parseGoodsInfo(String ret,String url) {
        if(ret==null){
            return null;
        }
        GoodsConfig goodsConfig = new GoodsConfig();
        String substring = ret.substring(ret.indexOf("(")+1,ret.lastIndexOf(")"));
        JSONObject parseObject = JSON.parseObject(substring);
        Integer code = parseObject.getInteger("code");
        if(code!=1){
            logger.error("错误码:{}",code);
            return null;
        }
        JSONObject data = parseObject.getJSONObject("data");
        goodsConfig.setName(data.getString("name"));
        JSONArray list = data.getJSONArray("list");
        List<String> versions = Lists.newArrayList();
        List<String> colors = Lists.newArrayList();
        for(int i =0;i<list.size();i++){
            JSONObject jsonObject = list.getJSONObject(i);
            if(jsonObject.getJSONArray("list")==null){
                String color = jsonObject.getString("value");
                colors.add(color);
            }else{
                String version = jsonObject.getString("value");
                versions.add(version);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                if(jsonArray.size()>colors.size()){
                    colors = Lists.newArrayList();
                    for(int j = 0;j<jsonArray.size();j++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                        String color = jsonObject1.getString("value");
                        colors.add(color);
                    }
                }
            }
        }
        goodsConfig.setUrl(url);
        goodsConfig.setVersion(versions);
        goodsConfig.setColor(colors);
        return goodsConfig;
    }
}
