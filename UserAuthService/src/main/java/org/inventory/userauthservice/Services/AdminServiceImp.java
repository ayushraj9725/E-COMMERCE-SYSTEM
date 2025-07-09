package org.inventory.userauthservice.Services;

import org.inventory.userauthservice.Configurations.Mapper;
import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignInRequestDto;
import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignupRequestDto;
import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignupResponseDto;
import org.inventory.userauthservice.DTOs.UserSignUpResponseDto;
import org.inventory.userauthservice.Models.Roles;
import org.inventory.userauthservice.Models.User;
import org.inventory.userauthservice.Repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdminServiceImp implements AdminService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Mapper mapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailServiceImp userDetailServiceImp;

    @Override
    public AdminSignupResponseDto signupAdmin(AdminSignupRequestDto adminSignupRequestDto) {

        if (!adminSignupRequestDto.getPassword().equals(adminSignupRequestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = new User();
        user.setName(adminSignupRequestDto.getName());
        user.setEmail(adminSignupRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(adminSignupRequestDto.getPassword()));  // i am encrypting password before saving
        user.setPhoneNumber(adminSignupRequestDto.getPhoneNumber());
        user.setAddress(adminSignupRequestDto.getAddress());
        user.setRoles(Set.of(Roles.ROLE_ADMIN));

        // here I am saving the user as object entity in a database
        User savedAdminAsUSer = userRepository.save(user);

        return mapper.modelMapper().map(savedAdminAsUSer, AdminSignupResponseDto.class);
    }

    @Override
    public Authentication signInAdmin(AdminSignInRequestDto adminSignInRequestDto) {
        String username = adminSignInRequestDto.getUsername();
        String password = adminSignInRequestDto.getPassword();

        UserDetails userDetails = userDetailServiceImp.loadUserByUsername(username);

        System.out.println(userDetails.getUsername());
        System.out.println(userDetails.getPassword());
        System.out.println(userDetails.getAuthorities());
        System.out.println("username: "+username + "password: "+password);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username,password,userDetails.getAuthorities())
        );
        System.out.println("Authentication successful " + authentication.getPrincipal());

        return authentication ;
    }

}
