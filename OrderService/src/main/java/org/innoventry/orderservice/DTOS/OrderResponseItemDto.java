package org.innoventry.orderservice.DTOS;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.innoventry.orderservice.Models.OrderItems;

@Data
@Builder
@AllArgsConstructor
public class OrderResponseItemDto {
    private Long id;
    private Long productId;
    private String productTitle;
    private Integer quantity;

    public static OrderResponseItemDto fromOrderItem(OrderItems item) {
        return OrderResponseItemDto.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productTitle(item.getProductTitle())
                .quantity(item.getQuantity())
                .build();
    }

}