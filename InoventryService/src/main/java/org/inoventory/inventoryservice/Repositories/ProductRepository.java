package org.inoventory.inventoryservice.Repositories;

import org.inoventory.inventoryservice.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByUserEmail(String userEmail);

    Optional<Product>  findByIdAndUserEmail(Long id , String userEmail);

    boolean existsByIdAndUserEmail(Long id, String userEmail);
}
