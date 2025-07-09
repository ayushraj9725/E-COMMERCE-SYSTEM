package org.inventory.userauthservice.Services.Sellers;

import org.inventory.userauthservice.DTOs.SellerRequestsDtos.SellerRegistrationRequest;
import org.inventory.userauthservice.DTOs.SellerRequestsDtos.SellerResponse;
import org.inventory.userauthservice.Models.ApplicationStatus;
import org.inventory.userauthservice.Models.Roles;
import org.inventory.userauthservice.Models.SellerApplication;
import org.inventory.userauthservice.Models.User;
import org.inventory.userauthservice.Repositories.SellerRepository;
import org.inventory.userauthservice.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.List;

@Service
public class SellerAppServiceImp implements SellerApplicationService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    public SellerResponse submitApplication(SellerRegistrationRequest request) {

        // Get current authenticated user
        System.out.println(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        System.out.println(email);
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        System.out.println("User loaded while application submission");

        // Build application
        SellerApplication sellerApplication = SellerApplication.builder()
                .user(user)
                .businessName(request.getBusinessName())
                .gstNumber(request.getGstNumber())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .pickupAddress(request.getPickupAddress())
                .status(ApplicationStatus.PENDING)
                .build();

        SellerApplication seller = sellerRepository.save(sellerApplication);

        return SellerResponse.fromSeller(user,seller);

    }

    @Override
    public List<SellerResponse> getPendingApplications() {
        List<SellerApplication> sellerApplications = sellerRepository.findByStatus(ApplicationStatus.PENDING)
                .orElseThrow(() -> new RuntimeException("No application found as PENDING STATUS"));
        List<SellerResponse> sellerResponses = new ArrayList<>();

        for(SellerApplication sellerApplication : sellerApplications){

            User user = userRepository.findById(sellerApplication.getUserId())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            sellerResponses.add(SellerResponse.fromSeller(user,sellerApplication));
        }

        return sellerResponses;
    }

    @Override
    public void approveApplication(Long applicationId) {

        SellerApplication sellerApplication = sellerRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        sellerApplication.setStatus(ApplicationStatus.APPROVED);
        sellerRepository.save(sellerApplication);

        User user = userRepository.findById(sellerApplication.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().add(Roles.ROLE_SELLER);
        userRepository.save(user);

    }

    @Override
    public void rejectApplication(Long applicationId) {
        SellerApplication app = sellerRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(ApplicationStatus.REJECTED);
        sellerRepository.save(app);
    }

}
