package org.inoventory.inventoryservice.DTOs;

import lombok.Data;
import org.inoventory.inventoryservice.Models.Category;

@Data
public class ProductRequestDto {

    private String title;

    private Category category;

    private Double price;

    private Integer stock;

    private String description ;

}
