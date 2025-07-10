package org.innoventry.orderservice.DTOS;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {

    private List<OrderRequestItemsDto> items;

}