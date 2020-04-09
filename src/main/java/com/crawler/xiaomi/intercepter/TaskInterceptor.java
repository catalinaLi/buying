package com.crawler.xiaomi.intercepter;

import com.crawler.xiaomi.annotation.Singleton;
import com.google.common.collect.Maps;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author: lllx
 * @Description: 任务拦截器
 * @Date: Created on 13:49 2020/4/8
 * @Modefied by:
 */
public class TaskInterceptor implements MethodInterceptor {

    private static Logger logger = LoggerFactory.getLogger(TaskInterceptor.class);

    private static Map<String,Boolean> singletonMap = Maps.newHashMap();

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
        /*if(method.getAnnotation(Retry.class)!=null){
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
        }*/

        //同步方法
        return proxy.invoke(obj,args);
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
}
