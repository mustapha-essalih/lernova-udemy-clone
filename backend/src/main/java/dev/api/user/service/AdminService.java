package dev.api.user.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.api.common.enums.Roles;
import dev.api.common.exceptions.BadRequestException;
import dev.api.common.exceptions.ResourceNotFoundException;
import dev.api.user.dto.ManagerRequest;
import dev.api.user.model.Managers;
import dev.api.user.repository.ManagersRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AdminService {

    private ManagersRepository managersRepository;
    private PasswordEncoder passwordEncoder;

    public List<Managers> getAllManagers() {
        
        return null;
    }

    public void createManager(ManagerRequest request) {

        Managers manager = managersRepository.findByUsername(request.getUsername()).orElse(null);

        if (manager != null) {
            throw new BadRequestException("username aleredy exists");
        }
        String passwordHashed = passwordEncoder.encode(request.getPassword());

        manager = new Managers();

        manager.setUsername(request.getUsername());
        manager.setRole(Roles.MANAGER);
        manager.setEnabled(true);
        manager.setPassword_hash(passwordHashed);

        managersRepository.save(manager);

    }

    public void updateManager(Integer managerId, ManagerRequest request) {

        Managers manager = managersRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("manager not found"));

        if (managersRepository.findByUsername(request.getUsername()).isPresent())
            throw new BadRequestException("username aleredy exists");

        manager.setUsername(request.getUsername());

        String passwordHashed = passwordEncoder.encode(request.getPassword());
        manager.setPassword_hash(passwordHashed);

        managersRepository.save(manager);

    }

    public void deleteManager(Integer managerId) {

        Managers manager = managersRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("manager not found"));

        managersRepository.delete(manager);

    }

}
