package com.dish.auth.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dish.auth.entity.AuthEntity;
import com.dish.auth.respository.AuthRepository;
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AuthEntity> authOptional = authRepository.findByEmail(username);
        if (!authOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        AuthEntity authEntity = authOptional.get();
        return new org.springframework.security.core.userdetails.User(authEntity.getEmail(), authEntity.getPassword(),
                new ArrayList<>());
    }
}

