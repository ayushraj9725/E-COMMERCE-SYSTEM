package org.innoventry.orderservice.InterServiceCommunication;

import org.innoventry.orderservice.DTOS.OrderRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "INVENTORY-SERVICE")  //name = application name, path = base path
public interface InventoryFeignClient {

    @PutMapping("/api/v1/products/reduce-stocks")  //use the same path and mapping
    Double reduceStocks(@RequestBody OrderRequestDto orderRequestDto);  //use the same method as order-service

    @GetMapping("/api/v1/products/get/{id}")
    String getProductTitle(@PathVariable Long id);

}