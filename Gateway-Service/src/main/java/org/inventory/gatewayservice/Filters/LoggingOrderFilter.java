package org.inventory.gatewayservice.Filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j                                  //  T is the type of configuration class you want to use for your filter. to use this we have to implement a constructor super(Config.class) , The apply(Config config) method, which returns a GatewayFilter
public class LoggingOrderFilter extends AbstractGatewayFilterFactory<LoggingOrderFilter.Config> {   // Extends AbstractGatewayFilterFactory to create a custom filter for API Gateway.This is a base class provided by Spring Cloud Gateway for creating custom route filters that can be reused and configured in .yml or Java.
                                                                                                    // LoggingOrderFilter.Config  This is a static inner class where you define the filter’s configurable fields.
    public LoggingOrderFilter() {   //constructor
        super(Config.class);  //Calls the parent constructor, passing the Config class.
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("Order filter pre: {} | message: {}", exchange.getRequest().getURI(), config.getMessage());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isLogResponse()) {
                    log.info("Order filter post: {}", exchange.getResponse().getStatusCode());
                }
            }));
        };
    }

    public static class Config {
        private String message;
        private boolean logResponse;

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isLogResponse() {
            return logResponse;
        }

        public void setLogResponse(boolean logResponse) {
            this.logResponse = logResponse;
        }
    }


    // we can use this


}