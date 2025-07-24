package dev.api.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import dev.api.admin.model.Admin;
import dev.api.admin.repository.AdminRepository;
import dev.api.instructors.model.Instructors;
import dev.api.instructors.repository.InstructorsRepository;
import dev.api.managers.model.Managers;
import dev.api.managers.repository.ManagersRepository;
import dev.api.students.model.Students;
import dev.api.students.repository.StudentsRepository;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private StudentsRepository studentsRepository;
    private InstructorsRepository instructorsRepository;
    private AdminRepository adminRepository;
    private ManagersRepository managersRepository;
    
    

    @Override
    public UserDetails loadUserByUsername(String username) {

        Students student = studentsRepository.findByUsername(username).orElse(null);
        if(student != null) 
            return student;

        Instructors instructor = instructorsRepository.findByUsername(username).orElse(null);
        
        if (instructor != null)
            return instructor;

        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null)
            return admin;

        Managers manager = managersRepository.findByUsername(username).orElse(null);
        if (manager != null)
            return manager;

        throw new ResourceAccessException("user not found"); 
    }
    
}