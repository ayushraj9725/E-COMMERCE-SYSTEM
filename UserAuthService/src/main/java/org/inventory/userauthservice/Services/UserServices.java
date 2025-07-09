package org.inventory.userauthservice.Services;

import org.inventory.userauthservice.DTOs.RoleAssignmentRequest;
import org.inventory.userauthservice.DTOs.UserSignInRequestDto;
import org.inventory.userauthservice.DTOs.UserSignUpRequestDto;
import org.inventory.userauthservice.DTOs.UserSignUpResponseDto;
import org.springframework.security.core.Authentication;


public interface UserServices {

    UserSignUpResponseDto RegisterUser(UserSignUpRequestDto userSignUpRequestDto);

    String assignRole(RoleAssignmentRequest request);

    Authentication Login(UserSignInRequestDto userSignInRequestDto);

}
