package org.inventory.userauthservice.Controllers;

import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignInRequestDto;
import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignupRequestDto;
import org.inventory.userauthservice.DTOs.AdminDtos.AdminSignupResponseDto;
import org.inventory.userauthservice.DTOs.SellerRequestsDtos.SellerResponse;
import org.inventory.userauthservice.DTOs.TokenResponse;
import org.inventory.userauthservice.DTOs.ValidateTokenResponse;
import org.inventory.userauthservice.Models.User;
import org.inventory.userauthservice.Repositories.UserRepository;
import org.inventory.userauthservice.Services.AdminService;
import org.inventory.userauthservice.Services.JWTService;
import org.inventory.userauthservice.Services.Sellers.SellerApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SellerApplicationService sellerApplicationService;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<?> adminSignUp(@RequestBody AdminSignupRequestDto adminSignupRequestDto){
        try{
            AdminSignupResponseDto response = adminService.signupAdmin(adminSignupRequestDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> adminSignIn(@RequestBody AdminSignInRequestDto adminSignInRequestDto){
        try{
            System.out.println("Before SignIn");
            Authentication authentication = adminService.signInAdmin(adminSignInRequestDto);
            if(authentication.isAuthenticated()){
                System.out.println("After SignIn");
                String username = adminSignInRequestDto.getUsername();
                User user = userRepository.findUserByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

                System.out.println("Before token creation");
                String token = jwtService.generateToken(user);
                System.out.println("After token creation");
                TokenResponse tokenResponse = new TokenResponse(token);
                return new ResponseEntity<>("Successfully Signed IN Admin : "+tokenResponse , HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Username or password incorrect",HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);        }
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

    @GetMapping("/seller-requests")
    public ResponseEntity<?> getAllPendingRequests(@RequestHeader("X-User-Role") String userRole) {
        if(!userRole.equals("ROLE_ADMIN")) return new ResponseEntity<>("Only Admin can Access this !",HttpStatus.FORBIDDEN);
        try{
            List<SellerResponse> getAllApplications = sellerApplicationService.getPendingApplications();
            return new ResponseEntity<>(getAllApplications,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/approve/{applicationId}")
    public ResponseEntity<?> approveSeller(@PathVariable Long applicationId, @RequestHeader("X-User-Role") String userRole) {
        if(!userRole.equals("ROLE_ADMIN")) return new ResponseEntity<>("Only Admin can Access this !",HttpStatus.FORBIDDEN);
        try{
            sellerApplicationService.approveApplication(applicationId);
            return new ResponseEntity<>("Seller approved" , HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject/{applicationId}")
    public ResponseEntity<String> rejectSeller(@PathVariable Long applicationId, @RequestHeader("X-User-Role") String userRole) {
        if(!userRole.equals("ROLE_ADMIN")) return new ResponseEntity<>("Only Admin can Access this !",HttpStatus.FORBIDDEN);
        try {
            sellerApplicationService.rejectApplication(applicationId);
            return new ResponseEntity<>("Seller rejected try again registration !!!" ,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }


}
