package org.inventory.userauthservice.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.inventory.userauthservice.DTOs.*;
import org.inventory.userauthservice.DTOs.SellerRequestsDtos.SellerRegistrationRequest;
import org.inventory.userauthservice.DTOs.SellerRequestsDtos.SellerResponse;
import org.inventory.userauthservice.Models.User;
import org.inventory.userauthservice.Repositories.UserRepository;
import org.inventory.userauthservice.Services.JWTService;
import org.inventory.userauthservice.Services.Sellers.SellerApplicationService;
import org.inventory.userauthservice.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/user")
public class UserController {

    @Autowired
    public UserServices userServices;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerApplicationService sellerApplicationService;

    @Autowired
    private UserDetailsService userDetailsService;


    @PostMapping("/signup")
    public ResponseEntity<?> Register(@RequestBody UserSignUpRequestDto userSignUpRequestDto){
        try{
            UserSignUpResponseDto userSignUpResponseDto = userServices.RegisterUser(userSignUpRequestDto);
            return new ResponseEntity<>(userSignUpResponseDto, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> Login(@RequestBody UserSignInRequestDto userSignInRequestDto){
        try{
            log.info("Before Authentication");
            //System.out.println(userSignInRequestDto);
            Authentication authentication = userServices.Login(userSignInRequestDto);
            //System.out.println(authentication);
            if(authentication.isAuthenticated()){
                String username = userSignInRequestDto.getUsername();
                User user = userRepository.findUserByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
                String jwtToken = jwtService.generateToken(user);
              //  System.out.println("Token is here : "+jwtToken);
              //  System.out.println("payload email | username : "+jwtService.extractUsername(jwtToken));

                TokenResponse tokenResponse = new TokenResponse(jwtToken);

                return new ResponseEntity<>("Successfully Logged In , here the token : " +tokenResponse ,HttpStatus.OK);
            }else{
                return new ResponseEntity<>("Username or password incorrect",HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/apply-seller")
    public ResponseEntity<?> applySellerViaUserController(@RequestBody SellerRegistrationRequest request,@RequestHeader("X-User-Role") String userRole) {
        if(!userRole.equals("ROLE_USER")) return new ResponseEntity<>("Only Authorize User Can Apply Seller ",HttpStatus.FORBIDDEN);

        try{
            SellerResponse sellerResponse = sellerApplicationService.submitApplication(request);
            return new ResponseEntity<>(sellerResponse,HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping("/validate")
    public ResponseEntity<?> validateUser(@RequestHeader("Authorization") String token){

        try{
            // Check if header starts with Bearer
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing or invalid Authorization header");
            }

            String pureToken = token.startsWith("Bearer ") ? token.substring(7):token ;
            String username = jwtService.extractUsername(pureToken); // extract email / username from the claims or payload using token

            // Load user by email
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(!jwtService.validateToken(pureToken,userDetails)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
            }

            // if token is found valid then extract the role / username /
            String email = jwtService.extractEmail(pureToken);

            List<String> roles = jwtService.extractRoles(pureToken);

            ValidateTokenResponse response = ValidateTokenResponse.fromToken(email,roles);

            return new ResponseEntity<>(response,HttpStatus.OK);

        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }


}
