package org.inoventory.inventoryservice.DTOs;

import lombok.Data;

@Data
public class ProductRequestDto {

    private String title;

    private Double price;

    private Integer stock;

    private String description ;

}
