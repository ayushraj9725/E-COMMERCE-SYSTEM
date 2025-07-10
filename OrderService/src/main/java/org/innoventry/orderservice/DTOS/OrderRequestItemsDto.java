package org.innoventry.orderservice.DTOS;

import lombok.Data;

@Data
public class OrderRequestItemsDto {

    private Long productId ;

    private Integer quantity ;

}
