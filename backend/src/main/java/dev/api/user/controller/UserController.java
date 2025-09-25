package dev.api.user.controller;

import dev.api.authentication.mapper.UserResponse;
import dev.api.common.ApiResponse;
import dev.api.user.service.UsersService;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;



@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private  UsersService userService;


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
    
        UserResponse currentUser = userService.getCurrentUser(principal.getName());

        return ResponseEntity.status(200).body(new ApiResponse<UserResponse>(true, null, currentUser));
    }
}
