package org.inventory.userauthservice.Models;

import jakarta.persistence.EntityListeners;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
public enum Roles {

    ROLE_USER ,

    ROLE_ADMIN ,

    ROLE_SELLER

}
