package org.inoventory.inventoryservice.Models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Double price;

    private Integer stock;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedBy
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedBy
    private Date updatedAt ;
}
