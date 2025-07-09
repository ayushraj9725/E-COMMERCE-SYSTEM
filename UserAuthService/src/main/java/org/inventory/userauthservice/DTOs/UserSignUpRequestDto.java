package org.inventory.userauthservice.DTOs;

import lombok.Data;

@Data
public class UserSignUpRequestDto {

    private String name ;

    private String email ;

    private String password ;

    private String confirmPassword ;

    private String phoneNumber ;

    private String address ;

}
