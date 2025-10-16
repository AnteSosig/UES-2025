package com.example.sss.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.sss.repozitorijumi")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.host}")
    private String elasticsearchHost;

    @Value("${elasticsearch.port}")
    private int elasticsearchPort;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchHost + ":" + elasticsearchPort)
                .withConnectTimeout(Duration.ofSeconds(30))
                .withSocketTimeout(Duration.ofSeconds(60))
                .build();
    }
}
