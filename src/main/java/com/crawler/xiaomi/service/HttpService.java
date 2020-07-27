package com.crawler.xiaomi.service;

import com.crawler.xiaomi.annotation.Service;
import com.crawler.xiaomi.manage.FilePathManage;
import com.crawler.xiaomi.pojo.Cookie;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @Author: lllx
 * @Description: 网络请求服务
 * @Date: Created on 16:03 2020/4/13
 * @Modefied by:
 */
@Service
public class HttpService {

    private static Logger logger = LoggerFactory.getLogger(HttpService.class);

    public static WebDriver driver = null;

    public HttpService() {
        driver = getChromeDriver();
    }

    public static WebDriver getChromeDriver(){
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, FilePathManage.chromeDriver);
        ChromeOptions options= new ChromeOptions();
        return new ChromeDriver(options);
    }

    public static PhantomJSDriver getPhantomJSDriver(){
        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", false);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, FilePathManage.exe);

        return new PhantomJSDriver(dcaps);
    }

    public String login() {
        driver.get("https://account.xiaomi.com/pass/serviceLogin?callback=https%3A%2F%2Forder.mi.com%2Flogin%2Fcallback%3Ffollowup%3Dhttps%253A%252F%252Fwww.mi.com%252Fbuy%252Fdetail%253Fproduct_id%253D10000239%26sign%3DODViYmY2MDdhZmFlYmY0NzIyODQ4NjE4ZmQwMzZlYTYzZjMzMWQzMQ%2C%2C&sid=mi_eshop&_bannerBiz=mistore&_qrsize=180");
        driver.findElement(By.id("username")).sendKeys("392235464@qq.com");
        driver.findElement(By.id("pwd")).sendKeys("lxln4425257");
        driver.findElement(By.id("login-button")).click();

        return driver.manage().getCookies().toString();
    }


    /**
     * 执行phantomjs
     * @param jsPath
     * @return
     */
    public String execute(String jsPath){
        return execute(jsPath, "");
    }

    public String execute(String jsPath,String url){
        Process p=null;
        try {
            p= Runtime.getRuntime().exec(FilePathManage.exe+" " +jsPath+" "+url);
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuffer sbf = new StringBuffer();
            String tmp = "";
            while((tmp = br.readLine())!=null){
                sbf.append(tmp);
            }
            br.close();
            is.close();
            return sbf.toString();
        } catch (Exception e) {
            logger.error("Phantomjs请求异常,js:{}",jsPath);
            return "";
        }finally {
            if(p!=null){
                p.destroy();
            }
        }
    }
    /**
     * 携带cookies请求网页
     * @param url
     * @param cookies
     * @return
     */
    public String getByCookies(String url,List<Cookie> cookies){
        CloseableHttpClient httpClient = createCookiesHttpClient();
        CloseableHttpResponse response=null;
        try{
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader(new BasicHeader("Cookie",builderCookiesStr(cookies)));
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Mobile Safari/537.36");
            response = httpClient.execute(httpGet);
            return toString(response);

        }catch(Exception e){
            logger.info("链接:"+url+"异常");
            return null;
        }finally{
            closeStream(response);
        }
    }

    public String getXiaomi(String url,String referer){
        CloseableHttpClient httpClient = createCookiesHttpClient();
        CloseableHttpResponse response=null;
        try{
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Referer",referer);
            httpGet.setHeader("Connection", "keep-alive");
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Mobile Safari/537.36");
            response = httpClient.execute(httpGet);
            return toString(response);

        }catch(Exception e){
            logger.info("链接:"+url+"异常");
            return null;
        }finally{
            closeStream(response);
        }
    }

    private String builderCookiesStr(List<Cookie> cookies) {
        StringBuilder str = new StringBuilder();
        cookies.forEach(o->{
            str.append(o.getName()).append("=").append(o.getValue()).append(";");
        });
        return str.toString();
    }
    /**
     * 携带cookies的HttpClient
     * @return
     */
    private CloseableHttpClient createCookiesHttpClient() {

        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).setConnectTimeout(10000).setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000).build();
        // 设置默认跳转以及存储cookie
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRedirectStrategy(new DefaultRedirectStrategy()).setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(new BasicCookieStore()).build();
        return httpClient;
    }



    /**
     * 关闭资源
     * @param streams
     * void
     */
    public void closeStream(Closeable... streams) {
        for(Closeable stream:streams){
            if(stream!=null){
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.error("资源关闭异常",e);
                }
            }

        }
    }

    public String toString(CloseableHttpResponse httpResponse){
        // 获取响应消息实体
        try{
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if(statusCode!=200){
                logger.error("状态值:{}",statusCode);
                return null;
            }
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity,"UTF-8");
        }catch(Exception e){
            logger.error("http返回数据转字符出现异常");
        }
        return null;

    }

}
