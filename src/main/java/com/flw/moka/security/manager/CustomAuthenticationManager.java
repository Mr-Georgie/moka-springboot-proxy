package com.flw.moka.security.manager;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.flw.moka.entity.models.ApiConsumer;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ApiConsumer apiConsumer = new ApiConsumer("flw", "password");
        // User user = new User("flw", "flw-pass");
        if (!apiConsumer.getPassword().equals(authentication.getCredentials().toString())) {
            throw new BadCredentialsException("Wrong Password");
        }

        return new UsernamePasswordAuthenticationToken(authentication.getName(), apiConsumer.getPassword());
    }

}

// User user = userServiceImpl.getUser(authentication.getName());