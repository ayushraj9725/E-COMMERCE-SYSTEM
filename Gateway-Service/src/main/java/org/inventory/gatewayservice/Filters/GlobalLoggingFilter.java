package org.inventory.gatewayservice.Filters;

import org.inventory.gatewayservice.DTOs.ValidateTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    @Autowired
    private WebClient.Builder webClientBuilder ;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = exchange.getRequest().getURI().getPath(); // extract a path for making register, login public through the user rest api

        if(path.contains("/user/signin") || path.contains("/user/signup") || path.contains("/admin/signin") || path.contains("/admin/signup")){
            return chain.filter(exchange); // pass to next filer from here
        }

        String rawAuth = request.getHeaders().getFirst("Authorization"); // check for token

        if(rawAuth == null || !rawAuth.startsWith("Bearer ")){
            return Mono.error(new RuntimeException("Missing or invalid Authorization header"));
        }

        String token = rawAuth.substring(7);

        String validationUri ; // handling both user and admin
        if (path.contains("/api/v1/auth/admin") || path.contains("/admin")) {
            validationUri = "http://USER-SERVICE/api/v1/auth/admin/validate";
        } else {
            validationUri = "http://USER-SERVICE/api/v1/auth/user/validate";
        }

        return webClientBuilder.build()
                .get()
                .uri(validationUri)
                .header("Authorization","Bearer " +token)
                .retrieve()
                .bodyToMono(ValidateTokenResponse.class)
                .flatMap(authResponse -> {
                    if (!authResponse.getRoles().contains("ROLE_SELLER") &&
                            exchange.getRequest().getURI().getPath().startsWith("/api/v1/products/add")) {
                        return Mono.error(new RuntimeException("Unauthorized seller"));
                    }
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Email" ,String.valueOf(authResponse.getEmail()))
                            .header("X-User-Role", String.join(",", authResponse.getRoles()))
                            .build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }


    /*
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
    */
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