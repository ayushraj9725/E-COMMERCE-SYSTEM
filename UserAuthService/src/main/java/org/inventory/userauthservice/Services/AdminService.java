package org.inventory.userauthservice.Services;

import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignInRequestDto;
import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignupRequestDto;
import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignupResponseDto;
import org.springframework.security.core.Authentication;

public interface AdminService {

    AdminSignupResponseDto signupAdmin(AdminSignupRequestDto adminSignupRequestDto);

    Authentication signInAdmin(AdminSignInRequestDto adminSignInRequestDto);

}
