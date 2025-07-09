package org.inventory.userauthservice.Repositories;

import org.inventory.userauthservice.Models.ApplicationStatus;
import org.inventory.userauthservice.Models.SellerApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<SellerApplication,Long> {

    Optional<List<SellerApplication>> findByStatus(ApplicationStatus status);

}
