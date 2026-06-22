package com.cybersec.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 通用网络安全智能体平台 — 启动入口
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.cybersec")
@MapperScan("com.cybersec.domain.mapper")
public class CybersecApplication {

    public static void main(String[] args) {
        SpringApplication.run(CybersecApplication.class, args);
    }
}
