package com.cathay.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抵繳系統主程式類
 *
 * <p>作為 Spring Boot 應用程式的入口點，負責啟動整個應用程式。
 * 同時配置了基本的 Spring Boot 功能和自動配置。
 *
 * <p>主要功能：
 * <ul>
 *   <li>啟動 Spring Boot 應用程式</li>
 *   <li>啟用 JPA 資料庫訪問</li>
 *   <li>啟用 Web MVC 功能</li>
 *   <li>配置資料初始化</li>
 * </ul>
 *
 * <p>啟動方式：
 * <pre>
 * java -jar offset-application.jar
 * </pre>
 *
 * @author Lucian
 * @version 1.0
 * @since 2025-03-06
 */
@SpringBootApplication
public class OffsetApplication {
    
    private static final Logger log = LoggerFactory.getLogger(OffsetApplication.class);
    
    @Autowired
    private Environment springEnv;
    
    @PostConstruct
    public void init() {
        String env = springEnv.getProperty("app.environment");
        log.info("Application Environment: '{}', length: {}", 
            env, env != null ? env.length() : 0);
        if (env != null) {
            log.info("Environment bytes: {}", env.getBytes());
        }
    }

    /**
     * 應用程式主入口點
     *
     * @param args 命令列參數
     */
    public static void main(String[] args) {
        SpringApplication.run(OffsetApplication.class, args);
    }
} 