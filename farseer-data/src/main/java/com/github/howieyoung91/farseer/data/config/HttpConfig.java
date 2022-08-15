package com.github.howieyoung91.farseer.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/08/14 12:25]
 */
@Configuration
public class HttpConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
