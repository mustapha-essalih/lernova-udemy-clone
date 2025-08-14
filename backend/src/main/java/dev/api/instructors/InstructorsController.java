package dev.api.instructors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.instructors.model.Instructors;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@AllArgsConstructor
@RequestMapping("/api/v1/instructors")
@RestController
public class InstructorsController {
    
    private InstructorsService instructorsService;

 


}
