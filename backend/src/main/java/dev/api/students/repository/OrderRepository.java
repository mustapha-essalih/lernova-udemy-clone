package dev.api.students.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.students.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
}
