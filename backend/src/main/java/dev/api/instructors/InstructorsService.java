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

    public UserResponse getInstructorInfos(String username) {
        Instructors instructor = instructorsRepository.findByUsername(username).get();
        return new UserResponse(instructor.getId(), instructor.getFirst_name(), instructor.getLast_name(), instructor.getEmail(), instructor.getUsername());
    }
    
}
