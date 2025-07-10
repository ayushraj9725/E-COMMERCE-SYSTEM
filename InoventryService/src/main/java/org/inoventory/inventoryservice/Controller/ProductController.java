package org.inoventory.inventoryservice.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.inoventory.inventoryservice.DTOs.OrderRequestDto;
import org.inoventory.inventoryservice.DTOs.ProductRequestDto;
import org.inoventory.inventoryservice.InterServiceCommunication.OrderFeignClient;
import org.modelmapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inoventory.inventoryservice.DTOs.ProductDto;
import org.inoventory.inventoryservice.Services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")  // we have to add "/api/v1" before adding this endpoint
public class ProductController {

    private final ProductService productService;
    private final OrderFeignClient orderFeignClient;

    private final ModelMapper modelMapper;
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequestDto productRequestDto,
                                        @RequestHeader("X-User-Role") String userRoles,
                                        @RequestHeader("X-User-Email") String userEmail){

        if(!userRoles.contains("ROLE_SELLER")) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try{
            ProductDto addedProduct = productService.addProduct(productRequestDto,userEmail);
            return new ResponseEntity<>(addedProduct,HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id ,
                                           @RequestBody ProductRequestDto productRequestDto,
                                           @RequestHeader("X-User-Role") String userRoles,
                                           @RequestHeader("X-User-Email") String userEmail){

        if(!userRoles.contains("ROLE_SELLER")) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        ProductDto updatedProduct = productService.updateProduct(id, productRequestDto,userEmail);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id ,
                                                @RequestHeader("X-User-Role") String userRoles,
                                                @RequestHeader("X-User-Email") String userEmail) {

        if(!userRoles.contains("ROLE_SELLER")) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try{
            boolean deleted = productService.deleteProduct(id,userEmail);
            if (deleted) {
                return ResponseEntity.ok("Product deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Product not found with ID: " + id);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/reduce-stocks")
    public ResponseEntity<Double> reduceStocks(@RequestBody OrderRequestDto orderRequestDto){
        Double totalPrice = productService.reduceStocks(orderRequestDto);
        return ResponseEntity.ok(totalPrice);
    }

    @GetMapping("/userId")  // this path for username or email passing
    public ResponseEntity<List<ProductDto>> getAllProducts( @RequestHeader("X-User-Role") String userRoles,
                                                          @RequestHeader("X-User-Email") String userEmail){
        if(!userRoles.contains("ROLE_SELLER")) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        log.info("Fetching all products via controller");

        try{
            List<ProductDto> inventories = productService.getAllProducts(userEmail);
            return new ResponseEntity<>(inventories,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id,
                                                     @RequestHeader("X-User-Role") String userRoles,
                                                     @RequestHeader("X-User-Email") String userEmail){
        log.info("Fetching product by id via controller");
        if(!userRoles.contains("ROLE_SELLER")) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try{
            ProductDto inventory = productService.getProductById(id,userEmail);
            return new ResponseEntity<>(inventory,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/get/{id}")
    public String getProductTitleById(@PathVariable Long id){
        log.info("Fetching product by id via controller and returning to order service");
        System.out.println(10);
        String title = productService.ProductTitle(id);
        System.out.println(title);
        return title;
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
