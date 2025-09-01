package dev.api.common;

import org.springframework.stereotype.Service;

import dev.api.admin.model.Admin;
import dev.api.admin.repository.AdminRepository;
import dev.api.authentication.responses.UserResponse;
import dev.api.instructors.model.Instructors;
import dev.api.instructors.repository.InstructorsRepository;
import dev.api.managers.model.Managers;
import dev.api.managers.repository.ManagersRepository;
import dev.api.students.model.Students;
import dev.api.students.repository.StudentsRepository;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class GeneraleService {
    

    private StudentsRepository studentsRepository;
    private InstructorsRepository instructorsRepository;
    private AdminRepository adminRepository;
    private ManagersRepository managersRepository;



    public UserResponse getUserInfos(String username, String jwt){
        Students student = studentsRepository.findByUsername(username).orElse(null);
        if(student != null) 
            return new UserResponse(student.getId(), student.getFirst_name(), student.getLast_name(), student.getEmail(), student.getUsername() , student.getRole().name() , jwt);

        Instructors instructor = instructorsRepository.findByUsername(username).orElse(null);
        
        if (instructor != null)
            return new UserResponse(instructor.getId(), instructor.getFirst_name(), instructor.getLast_name(), instructor.getEmail(), instructor.getUsername() , instructor.getRole().name(), jwt);

        Admin admin = adminRepository.findByUsername(username).orElse(null);


        if (admin != null)
            return new UserResponse(admin.getId(),  admin.getUsername() , admin.getRole().name(), jwt);

        Managers manager = managersRepository.findByUsername(username).orElse(null);


        if (manager != null)
            return new UserResponse(manager.getId(),  manager.getUsername() , manager.getRole().name() , jwt);

        return null;
    }


}
