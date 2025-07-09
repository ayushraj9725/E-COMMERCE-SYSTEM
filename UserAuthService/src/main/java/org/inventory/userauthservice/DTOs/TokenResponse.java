package org.inventory.userauthservice.DTOs;

import lombok.Data;

@Data

public class TokenResponse {

    private String token ;

    private String tokenType = "Bearer";

    public TokenResponse(String token){
        this.token = token ;
    }

}
