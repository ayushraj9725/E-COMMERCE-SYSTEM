package org.inventory.gatewayservice.Filters;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    //Implementing the filter() Method (Pre & Post Logging)
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // Pre-Filter Execution (Before Passing to Microservices)
        log.info("Logging from global pre: {}",exchange.getRequest().getURI());

        //Calling the Next Filter in the Chain
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

            // Post-Filter Execution (After Receiving Response from Microservices)
            log.info("Logging from global post: {}",exchange.getResponse().getStatusCode());
        }));

    }

    @Override
    public int getOrder() {  // to set execution priority (5)
        return 5;
    }

    /*
    GlobalFilter → A Spring Cloud Gateway interface for defining global filters applied to all requests.

    Ordered → Allows defining the execution order of the filter.

    Component → Registers this class as a Spring-managed bean, making it available for dependency injection.

    ServerWebExchange → Represents the web request & response during API Gateway processing.

    Mono<Void> → A reactive type indicating that the filter logic runs asynchronously.

    exchange → Represents the HTTP request and response.

    chain → Calls the next filter in the chain. then(Mono.fromRunnable(...)) ensures post-processing logic runs after the request is handled.

     */
}