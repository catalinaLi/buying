package com.crawler.xiaomi.db;

/**
 * @Author: lllx
 * @Description: 日志仓库
 * @Date: Created on 10:59 2020/4/13
 * @Modefied by:
 */
public class LogStorage {

    public static StringBuilder log = new StringBuilder();

    public static String lock = "lock";

    public static void addLog(String text) {
        synchronized (lock) {
            log.append(text);
        }
    }

    public static String getLog() {
        synchronized (lock) {
            String logText = log.toString();
            log.delete(0, log.length());
            return logText;
        }
    }
}