package org.inventory.userauthservice.DTOs.SellerRequestsDtos;

import lombok.*;
import org.inventory.userauthservice.Models.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String name ;

    private String email ;

    private String phoneNumber ;

    private String address ;

    public static UserResponse fromUser(User user){
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .build();
    }

}
