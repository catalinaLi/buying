package com.crawler.xiaomi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crawler.xiaomi.annotation.Async;
import com.crawler.xiaomi.annotation.Resource;
import com.crawler.xiaomi.annotation.Retry2;
import com.crawler.xiaomi.annotation.Service;
import com.crawler.xiaomi.annotation.Stop;
import com.crawler.xiaomi.annotation.Timing;
import com.crawler.xiaomi.db.GoodsInfoStorage;
import com.crawler.xiaomi.enums.TimingType;
import com.crawler.xiaomi.intercepter.MyThreadPool;
import com.crawler.xiaomi.manage.Config;
import com.crawler.xiaomi.manage.FilePathManage;
import com.crawler.xiaomi.manage.StatusManage;
import com.crawler.xiaomi.pojo.Cookie;
import com.crawler.xiaomi.pojo.GoodsConfig;
import com.crawler.xiaomi.pojo.User;
import com.crawler.xiaomi.utils.FileUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lllx
 * @Description: 小米抢购服务
 * @Date: Created on 16:14 2020/4/13
 * @Modefied by:
 */
@Service
public class XiaoMiService {

    private static Logger logger = LoggerFactory.getLogger(XiaoMiService.class);

    private ScheduledFuture<?> buy;

    private ScheduledFuture<?> stop;

    @Resource
    private HttpService httpService;

    public boolean islogin(){
        if(!FileUtil.isFile(FilePathManage.userConfig)){
            return false;
        }
        String miString = FileUtil.readFileToString(FilePathManage.userConfig);
        if(miString==null||miString.length()==0){
            return false;
        }
        User oldUser = JSON.parseObject(miString, User.class);
        if(oldUser==null){
            return false;
        }
        if(!oldUser.equals(Config.user)){
            return false;
        }
        if(oldUser.getCookies()==null||oldUser.getCookies().size()==0){
            return false;
        }
        boolean islogin = false;
        for(Cookie cookie : oldUser.getCookies()){
            if("userId".equals(cookie.getName())){
                islogin = true;
            }
            if("JSESSIONID".equals(cookie.getName())){
                return false;
            }
        }
        if(islogin){
            Config.user.setCookies(oldUser.getCookies());
            return true;
        }
        return false;

    }

    /**
     * 开始抢购
     */
    public void start() {
        //登录
        login();
        //购买
        buy = MyThreadPool.schedule(()->{
            logger.info("获取购买链接中。。。");
            getBuyUrl();
            buyGoodsTask();
        }, Config.customRule.getBuyTime()-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        //抢购时间截止
        stop = MyThreadPool.schedule(()->{
            stop("抢购时间截止，停止抢购");
        }, Config.customRule.getEndTime()-System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 每2秒开一个线程,去获取购买url
     */
    @Timing(initialDelay = 0, period = 1500, type = TimingType.FIXED_RATE, unit = TimeUnit.MILLISECONDS)
    public void getBuyUrl(){
        if(!StatusManage.isLogin){
            return;
        }
        queryBuyCartLink();
    }

    /**
     * httpClient加入购物车
     */
    @Timing(initialDelay = 0, period = 100, type = TimingType.FIXED_RATE, unit = TimeUnit.MILLISECONDS)
    public void buyGoodsTask() {
        if(StatusManage.isLogin&&Config.goodsInfo.getBuyUrls().size()>0){
            buy(Config.goodsInfo.getBuyUrls(),Config.user.getCookies());
        }
    }

    @Async(3)
    public void buy(List<String> buyUrl, List<Cookie> cookies){
        int count = StatusManage.cartCount.incrementAndGet();
        String url = selectOneUrl(buyUrl);
        long start = System.currentTimeMillis();
        String re = httpService.getByCookies(url, cookies);
        if(isBuySuccess(re)){
            logger.info("提交购物车成功({}),{}ms",count,System.currentTimeMillis()-start);
            stop("恭喜！抢购成功，赶紧去购物车付款吧！");
            if(StatusManage.submitCount.incrementAndGet()>1){
                return;
            }
            submitOrder();
        }

    }

    //判断是否抢购成功
    //jQuery111302798960934517918_1528978041106({"code":1,"message":"2173300005_0_buy","msg":"2173300005_0_buy"});
    public boolean isBuySuccess(String re) {
        if(re==null){
            return false;
        }
        try{
            String substring = re.substring(re.indexOf("(")+1,re.lastIndexOf(")"));
            JSONObject parseObject = JSON.parseObject(substring);
            Integer code = parseObject.getInteger("code");
            return code==1;
        }catch(Exception e){
            logger.error("parseBuyResult err:{}",re);
            return false;
        }
    }

    @Async(value = 3,interval = 1000)
    public void submitOrder() {
        long start = System.currentTimeMillis();
        String result = httpService.execute(FilePathManage.submitOrderJs);
        if(result.length()>0){
            logger.info("{},{}ms",result,System.currentTimeMillis()-start);
        }
    }

    public String selectOneUrl(List<String> buyUrl) {
        int randomNum = new Random().nextInt(2);
        if(buyUrl.size()==1){
            return buyUrl.get(0);
        }
        return buyUrl.get(buyUrl.size()-1-randomNum);
    }

    @Async
    public void queryBuyCartLink(){
        long startTime = System.currentTimeMillis();
        String result = httpService.execute(FilePathManage.queryBuyCartLinkJs);
        if(result.startsWith("http")){
            Config.goodsInfo.getBuyUrls().add(result);
            logger.info("已获取购买链接({}):{}ms",Config.goodsInfo.getBuyUrls().size(),System.currentTimeMillis()-startTime);
        }
    }

    @Async
    public void login() {
        if (!islogin()) {
            StatusManage.isLogin = false;
            toLogin();
            StatusManage.isLogin = true;
        }else {
            logger.info("用户:{} 已登录。",Config.user.getUserName());
            StatusManage.isLogin = true;
        }
    }

    @Retry2(success = "ok")
    public String toLogin() {
        long start = System.currentTimeMillis();
        FileUtil.writeToFile(JSON.toJSONString(Config.user), FilePathManage.userConfig);
        String result = httpService.execute(FilePathManage.loginJs);
        if(result.length()==0){
            logger.info("用户:{} 登录失败，正在重试。时间:{}ms",Config.user.getUserName(),System.currentTimeMillis()-start);
            return "fail";
        }
        if(result.equals("confine")){
            stop("用户被限制登录！");
            return "ok";
        }
        if(result.equals("pwd")){
            stop("用户名或密码错误！");
            return "ok";
        }
        List<Cookie> cookies = JSON.parseArray(result, Cookie.class);
        Config.user.setCookies(cookies);
        FileUtil.writeToFile(JSON.toJSONString(Config.user), FilePathManage.userConfig);
        logger.info("用户:{} 登录成功,时间:{}ms",Config.user.getUserName(),System.currentTimeMillis()-start);
        return "ok";
    }

    @Stop(methods = { "buyGoodsTask","getBuyUrl"})
    public void stop(String msg) {

        StatusManage.endMsg = msg;
        logger.info(msg);

        if(buy!=null){
            buy.cancel(false);//停止 购买定时器
        }

        if(stop!=null){
            stop.cancel(false);//停止 截止时间的定时器
        }

        StatusManage.isEnd = true;
    }


    /**
     * 商品搜索
     * @param name
     * @return
     */
    public String searchGoods(String name) {
        if (name.startsWith("http")) {
            if(!name.contains("product") && !name.contains("id")){
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
            String goodsId = url.split("=")[1];
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
