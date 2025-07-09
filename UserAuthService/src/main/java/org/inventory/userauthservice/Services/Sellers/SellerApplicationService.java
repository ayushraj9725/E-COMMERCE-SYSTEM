package org.inventory.userauthservice.Services.Sellers;


import org.inventory.userauthservice.DTOs.SellerRequestsDtos.SellerRegistrationRequest;
import org.inventory.userauthservice.DTOs.SellerRequestsDtos.SellerResponse;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Repository
public interface SellerApplicationService {

    SellerResponse submitApplication(SellerRegistrationRequest request); // letter we will make it as only void

    List<SellerResponse> getPendingApplications();

    void approveApplication(Long applicationId);

    void rejectApplication(Long applicationId);

}
