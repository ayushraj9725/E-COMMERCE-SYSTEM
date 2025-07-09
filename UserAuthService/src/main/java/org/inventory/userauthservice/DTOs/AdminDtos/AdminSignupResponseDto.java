package org.inventory.userauthservice.DTOs.AdminDtos;

import lombok.Data;

import java.util.Date;

@Data
public class AdminSignupResponseDto {

    private Long id;

    private String name ;

    private String email ;

    private String phoneNumber ;

    private String address ;

    private Date createdAt;

    private Date updatedAt ;

}
