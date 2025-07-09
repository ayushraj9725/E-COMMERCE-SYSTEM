package org.innoventry.orderservice.DTOS;

import lombok.Data;
import org.innoventry.orderservice.Models.OrderStatus;

import java.util.Date;
import java.util.List;

@Data
public class OrderResponseDto {

    private String username ;

    private Long orderId ;

    private List<OrderRequestItemDto> orderItems ;

    private OrderStatus orderStatus ;

    private Double totalPrice ;

    private Date createdAt ;

    private Date updatedAt ;

}
