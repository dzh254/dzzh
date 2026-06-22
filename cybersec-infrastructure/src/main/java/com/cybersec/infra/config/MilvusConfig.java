package com.cybersec.infra.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Milvus 向量数据库配置
 */
@Slf4j
@Configuration
public class MilvusConfig {

    @Value("${milvus.host:localhost}")
    private String host;

    @Value("${milvus.port:19530}")
    private int port;

    @Value("${milvus.username:}")
    private String username;

    @Value("${milvus.password:}")
    private String password;

    @Bean
    public MilvusServiceClient milvusClient() {
        ConnectParam.Builder builder = ConnectParam.newBuilder()
                .withHost(host)
                .withPort(port);

        if (username != null && !username.isBlank()) {
            builder.withAuthorization(username, password != null ? password : "");
        }

        MilvusServiceClient client = new MilvusServiceClient(builder.build());
        log.info("Milvus client configured: {}:{}", host, port);
        return client;
    }
}
