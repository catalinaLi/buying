package com.crawler.xiaomi.intercepter;

import com.crawler.xiaomi.annotation.Async;
import com.crawler.xiaomi.annotation.Retry;
import com.crawler.xiaomi.annotation.Retry2;
import com.crawler.xiaomi.annotation.Singleton;
import com.crawler.xiaomi.annotation.Stop;
import com.crawler.xiaomi.annotation.Timing;
import com.crawler.xiaomi.enums.TimingType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @Author: lllx
 * @Description: 任务拦截器
 * @Date: Created on 13:49 2020/4/8
 * @Modefied by:
 */
public class TaskInterceptor implements MethodInterceptor {

    private static Logger logger = LoggerFactory.getLogger(TaskInterceptor.class);

    private static Map<String,Boolean> singletonMap = Maps.newHashMap();

    private Map<String,ScheduledFuture<?>> futures = Maps.newHashMap();
    /**
     * 获取所有的单例方法
     * @param clazz
     */
    public TaskInterceptor(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Singleton singleton = method.getAnnotation(Singleton.class);
            if (singleton != null) {
                singletonMap.put(method.getName(), true);
            }
        }
    }

    /**
     * 拦截器
     * @param obj
     * @param method
     * @param args
     * @param proxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        //判断是否单例
        if (!checkReq(method)) {
            return null;
        }
        //重试方法
        if(method.getAnnotation(Retry.class)!=null){
            return retryTask(obj,method,args,proxy);
        }
        //重试方法2
        if(method.getAnnotation(Retry2.class)!=null){
            return retryTask2(obj,method,args,proxy);
        }
        //定时任务
        if(method.getAnnotation(Timing.class)!=null){
            return timingTask(obj,method,args,proxy);
        }
        //取消定时任务
        if(method.getAnnotation(Stop.class)!=null){
            stopTimingTask(obj,method,args,proxy);
            if(method.getAnnotation(Async.class)!=null){
                return asyncTask(obj,method,args,proxy);
            }else{
                return proxy.invokeSuper(obj, args);
            }
        }
        //异步方法
        if(method.getAnnotation(Async.class)!=null){
            return asyncTask(obj,method,args,proxy);
        }

        //同步方法
        return proxy.invoke(obj,args);
    }

    private Object asyncTask(Object obj, Method method, Object[] args, MethodProxy proxy) {
        Async async = method.getAnnotation(Async.class);
        for(int i =0;i<async.value();i++){
            MyThreadPool.execute(() -> {
                try {
                    proxy.invokeSuper(obj, args);
                } catch (Throwable e) {
                    logger.error("异步方法 :{} 发生错误:{}",method.getName(),e.getMessage());
                }
            });
            try {
                Thread.sleep(async.interval());
            } catch (InterruptedException e) {

            }
        }
        return null;
    }

    /**
     * 校验所有的单例方法
     * @param method
     * @return
     */
    private boolean checkReq(Method method) {
        synchronized (method) {
            if (singletonMap.containsKey(method.getName())) {
                if (singletonMap.get(method.getName())) {
                    singletonMap.put(method.getName(), false);
                    return true;
                }
                logger.info("{}不允许多次调用",method.getName());
                return false;
            }
            singletonMap.put(method.getName(), true);
            return true;
        }
    }

    private Object retryTask(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Retry retry = method.getAnnotation(Retry.class);
        int count = 0;
        Throwable result = new RuntimeException("retry error");
        while(count<=retry.count()){
            if(count!=0){
                logger.info("重试:{},方法:{},参数:{}",count,method.getName(),args);
            }
            try {
                return proxy.invokeSuper(obj, args);
            } catch (Throwable e) {
                result = e;
                logger.error("error:{},msg:{}",e.getClass().getName(),e.getMessage());
                List<Class<?>> clazzs = Lists.newArrayList(retry.retException());
                if(!clazzs.contains(e.getClass())){
                    throw e;
                }
            }
            count++;
        }
        throw result;
    }

    private Object retryTask2(Object obj, Method method, Object[] args, MethodProxy proxy) {
        Retry2 retry = method.getAnnotation(Retry2.class);
        Object result = null;
        while(!retry.success().equals(result)){
            try {
                Thread.sleep(retry.interval());
                result = proxy.invokeSuper(obj, args);
            } catch (Throwable e) {
                logger.error("error:{},msg:{}",e.getClass().getName(),e.getMessage());
            }
        }
        return result;
    }


    private Object timingTask(Object obj, Method method, Object[] args, MethodProxy proxy) {
        Timing timing = method.getAnnotation(Timing.class);
        Runnable runnable = () -> {
            try {
                proxy.invokeSuper(obj, args);
            } catch (Throwable e) {
                logger.error("定时任务:{} 发生错误:{}",method.getName(),e.getMessage());
            }
        };
        ScheduledFuture<?> future = null;
        if(timing.type() == TimingType.FIXED_DELAY){
            future = MyThreadPool.scheduleWithFixedDelay(runnable, timing.initialDelay(), timing.period(), timing.unit());
        }else{
            future = MyThreadPool.scheduleAtFixedRate(runnable, timing.initialDelay(), timing.period(), timing.unit());
        }
        futures.put(method.getName(), future);
        return null;
    }

    private void stopTimingTask(Object obj, Method method, Object[] args, MethodProxy proxy) {
        String[] methods = method.getAnnotation(Stop.class).methods();
        for(String methodName : methods){
            ScheduledFuture<?> futureByName = futures.get(methodName);
            if(futureByName!=null){
                synchronized (futureByName) {
                    if(!futureByName.isCancelled()){
                        logger.info("定时任务:{} 停止.",methodName);
                    };
                    futureByName.cancel(false);
                }

            }
        }
    }


}
