package org.innoventry.orderservice.Services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.innoventry.orderservice.DTOS.*;
import org.innoventry.orderservice.InterServiceCommunication.InventoryFeignClient;
import org.innoventry.orderservice.InterServiceCommunication.ProductClient;
import org.innoventry.orderservice.Models.OrderItems;
import org.innoventry.orderservice.Models.OrderStatus;
import org.innoventry.orderservice.Models.Orders;
import org.innoventry.orderservice.Repositories.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final InventoryFeignClient inventoryFeignClient;
    private final ProductClient productClient;

    @CircuitBreaker(name = "inventoryCircuitBreaker",fallbackMethod = "createOrderFallBack")
    @RateLimiter(name = "inventoryRateLimiter", fallbackMethod = "createOrderFallBack") // it will limit the no of request made with in a given time window (sliding window size) like say 10 requests in a second, Reject any request that exceeds the defined limit. Apply a fallback method to deal with rejected requests gracefully.
    @Retry(name = "inventoryRetry", fallbackMethod = "createOrderFallBack") // this help to keep our system reliable at calling time; retry depend upon name like what to do
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, String username) {   // now if we hit the api without running inventory service, we can still get response instead of showing internal errors or any server status or site can't reach, this is because of circuit breaker.
                                                                            // it will give empty orderRequestDto (because inventoryService throw the exception) retry automatically trigger and avoid the retry request after maxTimeLimit, and another side if multiple requests come continuously that will be blocked by ratelimiter

        Double totalPrice = inventoryFeignClient.reduceStocks(orderRequestDto);

        List<OrderItems> items = new ArrayList<>();

        Orders order = Orders.builder()
                .userEmail(username)
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.CONFIRMED)
                .build();

        for (OrderRequestItemsDto itemDto : orderRequestDto.getItems()) {
            String productTitle = productClient.getProductTitleById(itemDto.getProductId());
            OrderItems item = OrderItems.builder()
                    .productId(itemDto.getProductId())
                    .productTitle(productTitle)
                    .quantity(itemDto.getQuantity())
                    .order(order) // set the parent order
                    .build();

            items.add(item);
        }

        // Attach items to order
        order.setItems(items);

        // Save the order with items (cascade must be enabled)
        Orders savedOrder = orderRepository.save(order);

        List<OrderItems> savedItems = savedOrder.getItems();

        return OrderResponseDto.fromOrder(order,savedItems);

    }

    //fallback method
    public OrderRequestDto createOrderFallBack(OrderRequestDto orderRequestDto, Throwable throwable) {
        log.error("Fallback occurred due to : {}", throwable.getMessage());
        return new OrderRequestDto();
    }


    public boolean cancelOrder(Long orderId ,String username) {
        log.info("Cancelling order with ID: {}", orderId + " and " +username);

        Orders order = orderRepository.findByIdAndUserEmail(orderId,username)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            log.warn("Cannot cancel order ID {} as it is already {}", orderId, order.getOrderStatus());
            return false;
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order ID {} cancelled successfully", orderId);
        return true;
    }

    public boolean updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order status for ID: {}", orderId);

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Optional: Add rules to prevent invalid transitions
        OrderStatus currentStatus = order.getOrderStatus();
        if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot update status from " + currentStatus);
        }

        order.setOrderStatus(newStatus);
        orderRepository.save(order);
        log.info("Order ID {} status updated to {}", orderId, newStatus);
        return true;
    }


    public List<OrderResponseDto> getAllOrders(String username){
        log.info("Fetching all orders");
        List<Orders> orders = orderRepository.findAllByUserEmail(username);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponseDto.class))
                .toList();
    }

    public OrderResponseDto getOrderById(Long id,String username){
        log.info("Fetching order by id");
        Orders order = orderRepository.findByIdAndUserEmail(id,username).orElseThrow(()->new RuntimeException("Order not found"));
        return modelMapper.map(order, OrderResponseDto.class);
    }


}
