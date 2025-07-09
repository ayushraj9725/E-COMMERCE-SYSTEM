package org.inventory.userauthservice.Models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seller")
@EntityListeners(AuditingEntityListener.class)
public class SellerApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String businessName;
    private String gstNumber;
    private String accountNumber;
    private String ifscCode;
    private String pickupAddress;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status; // PENDING, APPROVED, REJECTED

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date submittedAt;

    public Long getUserId(){
        return user.getId();
    }
}

