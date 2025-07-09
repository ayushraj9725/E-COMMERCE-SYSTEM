package org.innoventry.orderservice.Repositories;

import org.innoventry.orderservice.Models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long> {

    Optional<Orders> findByIdAndUserEmail(Long orderId, String userEmail);

    List<Orders> findAllByUsername(String username);
}
