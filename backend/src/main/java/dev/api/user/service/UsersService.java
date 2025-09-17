package dev.api.user.service;

import org.springframework.stereotype.Service;

import dev.api.authentication.mapper.UserResponse;
import dev.api.common.GeneraleService;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class UsersService {

    private GeneraleService generaleService;




    
    public UserResponse getCurrentUser(String username) {
        
        return generaleService.getUserInfos(username, null);
    }

}
