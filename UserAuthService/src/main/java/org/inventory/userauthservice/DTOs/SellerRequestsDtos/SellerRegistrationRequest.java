package org.inventory.userauthservice.DTOs.SellerRequestsDtos;

import lombok.Data;

@Data
public class SellerRegistrationRequest {

    private String businessName;

    private String gstNumber;

    private String accountNumber;

    private String ifscCode;

    private String pickupAddress;

}
