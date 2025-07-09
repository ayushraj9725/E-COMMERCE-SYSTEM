package org.inventory.userauthservice.DTOs;

import lombok.Data;

@Data
public class UserSignInRequestDto {

    private String username ;

    private String password ;

}
