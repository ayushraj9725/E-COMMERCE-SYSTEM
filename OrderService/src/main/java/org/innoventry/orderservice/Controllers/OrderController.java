package org.innoventry.orderservice.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innoventry.orderservice.DTOS.OrderRequestDto;
import org.innoventry.orderservice.DTOS.OrderResponseDto;
import org.innoventry.orderservice.Models.OrderStatus;
import org.innoventry.orderservice.Services.OrderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/helloOrders")
    public String helloOrders(){
        return "Hello from OrderService";
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto orderRequestDto,
                                                       @RequestHeader("X-User-Email") String username){
        try{
            OrderResponseDto orderRequestDto1 = orderService.createOrder(orderRequestDto,username);
            return new ResponseEntity<>(orderRequestDto1, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/{id}/cancel-order")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id,
                                              @RequestHeader("X-User-Email") String username) {
        try{
            boolean result = orderService.cancelOrder(id,username);

            if (result) {
                return ResponseEntity.ok("Order cancelled successfully.");
            } else {
                return ResponseEntity.badRequest().body("Order cannot be cancelled.");
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Order cannot be cancelled.");
        }

    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request, @RequestHeader("X-User_Role") String userRole) {

        if(userRole.equals("ROLE_USER")) return new ResponseEntity<>("Can't updated order by user : ",HttpStatus.FORBIDDEN);

        OrderStatus newStatus = OrderStatus.valueOf(request.get("status").toUpperCase());
        boolean updated = orderService.updateOrderStatus(id, newStatus);
        return updated
                ? ResponseEntity.ok("Order status updated to " + newStatus)
                : ResponseEntity.badRequest().body("Failed to update status.");

    }


    @GetMapping
    public ResponseEntity<?> getAllOrders(@RequestHeader("X-User-Email")String username){
        log.info("Fetching all orders via controller");
        try{
            List<OrderResponseDto> orders = orderService.getAllOrders(username);
            return new ResponseEntity<>(orders,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id,@RequestHeader("X-User-Email")String username){
        log.info("Fetching order by id via controller");
        try{
            OrderRequestDto order = orderService.getOrderById(id,username);
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

    }

}
