package org.inoventory.inventoryservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.inoventory.inventoryservice.DTOs.OrderRequestDto;
import org.inoventory.inventoryservice.DTOs.ProductRequestDto;
import org.inoventory.inventoryservice.InterServiceCommunication.OrderFeignClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inoventory.inventoryservice.DTOs.ProductDto;
import org.inoventory.inventoryservice.Services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")  // we have to add "/api/v1" before adding this endpoint
public class ProductController {

    private final ProductService productService;
    private final OrderFeignClient orderFeignClient;

    @PostMapping("/add")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductRequestDto productRequestDto){
        ProductDto addedProduct = productService.addProduct(productRequestDto);
        return ResponseEntity.ok(addedProduct);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id , @RequestBody ProductRequestDto productRequestDto){
        ProductDto updatedProduct = productService.updateProduct(id, productRequestDto);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);

        if (deleted) {
            return ResponseEntity.ok("Product deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found with ID: " + id);
        }
    }

    @PutMapping("reduce-stocks")
    public ResponseEntity<Double> reduceStocks(@RequestBody OrderRequestDto orderRequestDto){
        Double totalPrice = productService.reduceStocks(orderRequestDto);
        return ResponseEntity.ok(totalPrice);
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        log.info("Fetching all products via controller");
        List<ProductDto> inventories = productService.getAllProducts();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        log.info("Fetching product by id via controller");
        ProductDto inventory = productService.getProductById(id);
        return ResponseEntity.ok(inventory);
    }

    /*

    //from "org.springframework.cloud.client.discovery.DiscoveryClient" package
    private final DiscoveryClient discoveryClient;
    // from "org.springframework.web.client.RestClient" package
    private final RestClient restClient;  // used for third party api call, we need to configure it

    */

    /*
    // direct call using inter-service communication by rest or web client
    @GetMapping("/fetchOrder")
    public String fetchFromOrderService() {

        // from "org.springframework.cloud.client.ServiceInstance" package
        ServiceInstance orderService = discoveryClient.getInstances("ORDER-SERVICE").get(0);  //here this service id came from application name only (go to order application properties you can see the application name)
        // getFirst() for getting first instance
        return restClient.get()
                .uri(orderService.getUri()+"/api/v1/orders/helloOrders")  //got the correct url of orders api
                .retrieve()
                .body(String.class);
    }
    */

    // call by the Main API gateway

    /*
    @GetMapping("/fetchOrder")
    public String fetchFromOrderService(HttpServletRequest httpServletRequest) {
        log.info(httpServletRequest.getHeader("X-Custom-Header"));

        ServiceInstance orderService = discoveryClient.getInstances("order-service").get(0);  //here this service id came from application name only (go to order application properties u can see the application name
        return restClient.get()
                .uri(orderService.getUri()+"/api/v1/orders/helloOrders")
                .retrieve()
                .body(String.class);
    }
    */

    @GetMapping("/fetchOrder")
    public String fetchFromOrderService(HttpServletRequest httpServletRequest) {
        log.info(httpServletRequest.getHeader("X-Custom-Header"));

        return orderFeignClient.helloOrders();  //instead of restClient, we use orderFeignClient
    }


}
