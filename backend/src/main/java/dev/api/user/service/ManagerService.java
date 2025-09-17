package dev.api.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.api.courses.model.Course;
import dev.api.courses.repository.CoursesRepository;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class ManagerService {

    private CoursesRepository coursesRepository;

    public List<Course> getPendingCourses() {
    
        // get them from cache
        List<Course> courses = coursesRepository.findAllPendingCourses();

        // use map struct anad handle the upload
        return null;
    }

    public Object getInstructorsWithCourses() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getInstructorsWithCourses'");
    }

    public Object previewCourse(Long courseId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'previewCourse'");
    }



}
