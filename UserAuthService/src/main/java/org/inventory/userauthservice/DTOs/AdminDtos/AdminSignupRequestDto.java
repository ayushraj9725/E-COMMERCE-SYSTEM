package org.inventory.userauthservice.DTOs.AdminDtos;

import lombok.Data;

@Data
public class AdminSignupRequestDto {

    private String name ;

    private String email ;

    private String password ;

    private String confirmPassword ;

    private String phoneNumber ;

    private String address ;

}
