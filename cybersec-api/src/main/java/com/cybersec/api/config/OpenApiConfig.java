package com.cybersec.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) 文档配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("通用网络安全智能体平台 API")
                        .description("""
                                第一阶段 MVP 接口文档
                                核心链路: 告警摄入 → LLM智能研判 → RAG知识检索 → 安全护栏
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DBAPPSecurity")
                                .email("ai-lab@dbappsecurity.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("本地开发环境")
                ));
    }
}
