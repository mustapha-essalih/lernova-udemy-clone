package dev.api.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.courses.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
}
