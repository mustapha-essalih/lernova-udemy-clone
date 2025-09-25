package dev.api.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.user.model.Managers;

@Repository
public interface ManagersRepository extends JpaRepository<Managers, Integer>{
    Optional<Managers> findByUsername(String username);
}
