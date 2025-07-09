package org.inventory.userauthservice.Services;

import org.inventory.userauthservice.Configurations.Mapper;
import org.inventory.userauthservice.DTOs.RoleAssignmentRequest;
import org.inventory.userauthservice.DTOs.UserSignInRequestDto;
import org.inventory.userauthservice.DTOs.UserSignUpRequestDto;
import org.inventory.userauthservice.DTOs.UserSignUpResponseDto;
import org.inventory.userauthservice.Models.Roles;
import org.inventory.userauthservice.Models.User;
import org.inventory.userauthservice.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImp implements UserServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public Mapper mapper ;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailServiceImp userDetailServiceImp;

    @Override
    public UserSignUpResponseDto RegisterUser(UserSignUpRequestDto userSignUpRequestDto) {

        if (!userSignUpRequestDto.getPassword().equals(userSignUpRequestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = new User();
        user.setName(userSignUpRequestDto.getName());
        user.setEmail(userSignUpRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userSignUpRequestDto.getPassword()));  // i am encrypting password before saving
        user.setPhoneNumber(userSignUpRequestDto.getPhoneNumber());
        user.setAddress(userSignUpRequestDto.getAddress());
        user.setRoles(Set.of(Roles.ROLE_USER));

        // here I am saving the user as object entity in a database
        User savedUser = userRepository.save(user);

        return mapper.modelMapper().map(savedUser,UserSignUpResponseDto.class);
    }

    public String assignRole(RoleAssignmentRequest request){
        String email = request.getEmail();
        Roles role = mapper.modelMapper().map(request.getRole().toUpperCase(),Roles.class);

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);  // ✅ add without removing others
            userRepository.save(user);
        }
        return "Role Assigned" ;
    }

    public Authentication Login(UserSignInRequestDto userSignInRequestDto){
        String username = userSignInRequestDto.getUsername();
        String password = userSignInRequestDto.getPassword();

        UserDetails userDetails = userDetailServiceImp.loadUserByUsername(username);

//        System.out.println(userDetails.getUsername());
//        System.out.println(userDetails.getPassword());
//        System.out.println(userDetails.getAuthorities());
//        System.out.println("username: "+username + "password: "+password);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username,password,userDetails.getAuthorities())
        );
//        System.out.println("Authentication successful " + authentication.getPrincipal());

        return authentication ;
    }
}
