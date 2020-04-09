package com.crawler.xiaomi.manage;

import com.crawler.xiaomi.annotation.Controller;
import com.crawler.xiaomi.annotation.Service;
import com.crawler.xiaomi.intercepter.TaskInterceptor;
import com.google.common.collect.Maps;
import net.sf.cglib.proxy.Enhancer;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * @Author: lllx
 * @Description: bean工厂，简易spring
 * @Date: Created on 12:43 2020/4/8
 * @Modefied by:
 */
public class ServiceFactory {

    private static Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

    private static final String packageName = "com.crawler.xiaomi";

    private static Map<String,Object> serviceMap = null;

    private static void initService() {
        serviceMap = Maps.newHashMap();
        Reflections reflections = new Reflections();
        Set<Class<?>> controllerClasss = reflections.getTypesAnnotatedWith(Controller.class);
        Set<Class<?>> serviceClasss = reflections.getTypesAnnotatedWith(Service.class);
        serviceClasss.addAll(controllerClasss);
        for (Class<?> classs : serviceClasss) {
            createInstance(classs);
        }

    }
    private static void createInstance(Class<?> clazz) {
        if (!serviceMap.containsKey(clazz.getName())) {
            return;
        }
        if (clazz.getAnnotation(Controller.class) == null && clazz.getAnnotation(Service.class) == null) {
            return;
        }
        try {
            //创建Enhancer对象，类似于JDK动态代理的Proxy类，下一步就是设置几个参数
            Enhancer enhancer = new Enhancer();
            //设置目标类的字节码文件
            enhancer.setSuperclass(clazz);
            //设置回调函数
            enhancer.setCallback(new TaskInterceptor(clazz));
            //这里的creat方法就是正式创建代理类
            Object create = enhancer.create();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!serviceMap.containsKey(field.getType().getName())) {
                    createInstance(field.getClass());
                }
                Object ob = serviceMap.get(field.getType().getName());
                field.set(create,ob);
            }
            serviceMap.put(clazz.getName(), create);
            logger.info("{}初始化创建成功",clazz.getName());
        } catch (IllegalAccessException e) {
            logger.error("{}初始化创建失败",clazz.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz) {
        synchronized (packageName) {
            if (serviceMap == null) {
                initService();
            }
        }
        return (T) serviceMap.get(clazz.getName());
    }



}
