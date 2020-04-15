package com.crawler.xiaomi.service;

import com.crawler.xiaomi.annotation.Async;
import com.crawler.xiaomi.annotation.Service;
import com.crawler.xiaomi.annotation.Singleton;
import com.crawler.xiaomi.db.LogStorage;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Scanner;


/**
 * @Author: lllx
 * @Description: 日志服务
 * @Date: Created on 10:47 2020/4/13
 * @Modefied by:
 */
@Service
public class LogService {

    @Async
    @Singleton
    @SuppressWarnings("resource")
    public void readLogs() {
        try {
            Appender appender = Logger.getRootLogger().getAppender("stdout");
            PipedReader reader = new PipedReader();
            PipedWriter writer = new PipedWriter(reader);
            ((WriterAppender)appender).setWriter(writer);
            while (true) {
                Scanner scanner = new Scanner(reader);
                while (scanner.hasNextLine()) {
                    LogStorage.addLog(scanner.nextLine());
                }
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
