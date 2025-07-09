package org.inventory.userauthservice.DTOs.SellerRequestsDtos;

import lombok.*;
import org.inventory.userauthservice.Models.ApplicationStatus;
import org.inventory.userauthservice.Models.SellerApplication;
import org.inventory.userauthservice.Models.User;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerResponse {

    private UserResponse user ;

    private Long sellerId ;

    private String businessName;

    private String gstNumber;

    private String accountNumber;

    private String ifscCode;

    private ApplicationStatus status;

    private String pickupAddress;

    private Date submittedAt ;
    // write response

    public static SellerResponse fromSeller(User user , SellerApplication sellerApplication){
        return SellerResponse.builder()
                .user(UserResponse.fromUser(user))
                .sellerId(sellerApplication.getId())
                .businessName(sellerApplication.getBusinessName())
                .gstNumber(sellerApplication.getGstNumber())
                .accountNumber(sellerApplication.getAccountNumber())
                .ifscCode(sellerApplication.getIfscCode())
                .status(sellerApplication.getStatus())
                .pickupAddress(sellerApplication.getPickupAddress())
                .submittedAt(sellerApplication.getSubmittedAt())
                .build();
    }

}
