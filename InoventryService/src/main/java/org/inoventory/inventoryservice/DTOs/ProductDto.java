package org.inoventory.inventoryservice.DTOs;

import lombok.Data;

import java.util.Date;

@Data
public class ProductDto {

    private String userEmail;

    private Long id;

    private String title;

    private Double price;

    private Integer stock;

    private String description ;

    private Date createdAt;

    private Date updatedAt;

}
