package org.innoventry.orderservice.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innoventry.orderservice.DTOS.OrderRequestDto;
import org.innoventry.orderservice.Models.OrderStatus;
import org.innoventry.orderservice.Services.OrderService;
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
    public ResponseEntity<OrderRequestDto> createOrder(@RequestBody OrderRequestDto orderRequestDto){

        OrderRequestDto orderRequestDto1 = orderService.createOrder(orderRequestDto);
        return ResponseEntity.ok(orderRequestDto1);

    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        boolean result = orderService.cancelOrder(id);

        if (result) {
            return ResponseEntity.ok("Order cancelled successfully.");
        } else {
            return ResponseEntity.badRequest().body("Order cannot be cancelled.");
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {

        OrderStatus newStatus = OrderStatus.valueOf(request.get("status").toUpperCase());

        boolean updated = orderService.updateOrderStatus(id, newStatus);

        return updated
                ? ResponseEntity.ok("Order status updated to " + newStatus)
                : ResponseEntity.badRequest().body("Failed to update status.");
    }


    @GetMapping
    public ResponseEntity<List<OrderRequestDto>> getAllOrders(){
        log.info("Fetching all orders via controller");
        List<OrderRequestDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderRequestDto> getOrderById(@PathVariable Long id){
        log.info("Fetching order by id via controller");
        OrderRequestDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

}
