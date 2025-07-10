package org.innoventry.orderservice.InterServiceCommunication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProductClient {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public String getProductTitleById(Long productId) {
        return webClientBuilder.build()
                .get()
                .uri("http://INVENTORY-SERVICE/api/v1/products/get/" +productId)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // blocking call for simplicity can be reactive
    }
    // if we whenever need the all product data here, we can call the ProductDto and fetch data simply
}

