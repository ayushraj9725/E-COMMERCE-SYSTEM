package org.inoventory.inventoryservice.Repositories;

import feign.Param;
import org.springframework.data.domain.Page;
import org.inoventory.inventoryservice.Models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByUserEmail(String userEmail);

    Optional<Product>  findByIdAndUserEmail(Long id , String userEmail);

    boolean existsByIdAndUserEmail(Long id, String userEmail);

    @Query("SELECT p.title FROM Product p WHERE p.id = :id")
    String findProductTitleById(Long id);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Product> searchProducts(@Param("query") String query, Pageable pageable);


}
