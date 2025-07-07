package org.inventory.gatewayservice.Configurations;

import org.inventory.gatewayservice.Filters.LoggingOrderFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder , LoggingOrderFilter loggingOrderFilter){

        LoggingOrderFilter.Config config = new LoggingOrderFilter.Config();
        config.setMessage("Logging Order route");
        config.setLogResponse(true);

        return builder.routes()
                .route("inventory-service" , r -> r
                        .path("/api/v1/products/**")
//                       .filters(f -> f
//                                .stripPrefix(2))   // remove the first two segments from a path
//                              .redirect(302,"http://localhost:9020")) // Redirects to URL with status code
                        .uri("lb://INVENTORY-SERVICE"))

                .route("order-service",r->r
                        .path("/api/v1/orders/**")
                        .filters(f->f
                               .filter(loggingOrderFilter.apply(config))
//                              .stripPrefix(2))
//                              .addRequestHeader("X-Custom-Header","ABCD") // adding the custom header
                       )
                        .uri("lb://ORDER-SERVICE"))
                .route("order-service",r -> r
                        .path("/api/v1/auth/**")
                        .uri("lb://USER-SERVICE"))

                .build();

    }

}
