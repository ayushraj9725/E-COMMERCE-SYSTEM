package org.innoventry.orderservice.DTOS;

import jakarta.transaction.Transactional;
import lombok.*;
import org.innoventry.orderservice.Models.OrderItems;
import org.innoventry.orderservice.Models.OrderStatus;
import org.innoventry.orderservice.Models.Orders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class OrderResponseDto {

    private String username ;

    private Long orderId ;

    private List<OrderResponseItemDto> orderItems ;

    private OrderStatus orderStatus ;

    private Double totalPrice ;

    private Date createdAt ;

    private Date updatedAt ;

    public static OrderResponseDto fromOrder(Orders order, List<OrderItems> orderItems){

        List<OrderResponseItemDto> items = new ArrayList<>();
        for(OrderItems orderItems1 : orderItems){
            OrderResponseItemDto itemDto = OrderResponseItemDto.fromOrderItem(orderItems1);
            items.add(itemDto);
        }

        return OrderResponseDto.builder()
                .username(order.getUserEmail())
                .orderId(order.getId())
                .orderItems(items)
                .orderStatus(order.getOrderStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

}
