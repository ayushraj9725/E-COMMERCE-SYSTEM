package org.inoventory.inventoryservice.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Client {
    @Bean
    public RestClient getClient(){
        return RestClient.builder().build();
    }
}
