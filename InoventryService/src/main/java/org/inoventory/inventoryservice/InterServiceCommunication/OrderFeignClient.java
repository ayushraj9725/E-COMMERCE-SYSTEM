package org.inoventory.inventoryservice.InterServiceCommunication;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "ORDER-SERVICE")  //name = application name, path = base path
public interface OrderFeignClient {

    @GetMapping("/api/v1/orders/helloOrders")  //use the same path and mapping
    String helloOrders();   //use the same method as order-service

}