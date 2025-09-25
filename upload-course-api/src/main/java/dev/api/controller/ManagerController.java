package dev.api.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.api.service.ManagerService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;


@AllArgsConstructor
@RequestMapping("/api/v1/manager/courses")
@RestController
public class ManagerController {
    
    private  ManagerService managerService;



    @GetMapping(value = "/lesson/{lessonId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<Void> getLessonFile(@PathVariable String lessonId, ServerHttpResponse response) {
       return managerService.getLessonFile(lessonId , response);
    }


    @GetMapping(value = "/resource/{resourceId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<Void> getResourceFile(@PathVariable String resourceId, ServerHttpResponse response) {
        return managerService.getResourceFile(resourceId, response);
    }

     
}
