package org.inventory.gatewayservice.DTOs;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateTokenResponse {

    private String email ;

    private List<String> roles ;

    public static ValidateTokenResponse fromToken(String email ,List<String> roles){
        return ValidateTokenResponse.builder()
                .email(email)
                .roles(roles)
                .build();
    }

}

