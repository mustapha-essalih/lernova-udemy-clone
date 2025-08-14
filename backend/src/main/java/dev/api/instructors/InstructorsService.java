package dev.api.instructors;

import org.springframework.stereotype.Service;

import dev.api.authentication.responses.UserResponse;
import dev.api.instructors.model.Instructors;
import dev.api.instructors.repository.InstructorsRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class InstructorsService {

    private InstructorsRepository instructorsRepository;

 
    
}
