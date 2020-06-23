package com.crawler.xiaomi;

import com.crawler.xiaomi.manage.FilePathManage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.setProperty("webdriver.chrome.driver", FilePathManage.chromeDriver);
//        ChromeOptions options= new ChromeOptions();
//        options.addArguments("blink-settings=imagesEnabled=false");  //禁止加载图片
//        options.addArguments("--disable-gpu");//禁止gpu加速，否则在linux下会有bug
//        options.addArguments("--no-sandbox");//linux启动必须
//        options.addArguments("--window-size=1366,768");//指定浏览器分辨率
//        options.addArguments("--hide-scrollbars");//隐藏滚动条, 应对一些特殊页面
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.baidu.com/s?wd=mac%20idea%20setter&rsv_spt=1&rsv_iqid=0xfd9f53c600045370&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=1&rsv_dl=tb&oq=Desired%2526lt%253Bapabilities&rsv_btype=t&inputT=5702&rsv_t=cf89ZqZkn7WH3SRrAtVRJxvM5WMfXkFKmTt3HFHoMj%2FGGqzLVBngU%2FpsqkkXFn%2BnD1ng&rsv_pq=e53216ec00048965&rsv_sug3=28&rsv_sug1=6&rsv_sug7=100&rsv_sug2=0&rsv_sug4=6540");
        driver.quit();
    }
}
