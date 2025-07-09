package org.inventory.userauthservice.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.inventory.userauthservice.Models.Roles;

@Data
@Setter
@Getter
public class RoleAssignmentRequest {
    private String email;  // existing user
    private String role;   // e.g. "ROLE_SELLER" or "ROLE_ADMIN"
}

