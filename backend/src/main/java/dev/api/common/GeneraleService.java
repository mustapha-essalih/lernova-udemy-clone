package dev.api.common;

import org.springframework.stereotype.Service;

import dev.api.authentication.responses.UserResponse;
import dev.api.instructors.model.Instructors;
import dev.api.instructors.repository.InstructorsRepository;
import dev.api.students.model.Students;
import dev.api.students.repository.StudentsRepository;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class GeneraleService {
    

    private StudentsRepository studentsRepository;
    private InstructorsRepository instructorsRepository;
    

    public UserResponse getUserInfos(String username){
         Students student = studentsRepository.findByUsername(username).orElse(null);
        if(student != null) 
            return new UserResponse(student.getId(), student.getFirst_name(), student.getLast_name(), student.getEmail(), student.getUsername());

        Instructors instructor = instructorsRepository.findByUsername(username).orElse(null);
        
        if (instructor != null)
            return new UserResponse(instructor.getId(), instructor.getFirst_name(), instructor.getLast_name(), instructor.getEmail(), instructor.getUsername());

        return null;
    }


}
