package org.inoventory.inventoryservice.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {

    private List<OrderRequestItemDto> items;

}