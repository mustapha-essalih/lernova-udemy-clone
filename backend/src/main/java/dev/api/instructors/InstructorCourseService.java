package dev.api.instructors;

import org.springframework.stereotype.Service;

import dev.api.courses.model.Courses;



@Service
public class InstructorCourseService {

    public Courses createCourse(Integer instructorId) {
        
        System.out.println(instructorId);


        return null;
    }

}
