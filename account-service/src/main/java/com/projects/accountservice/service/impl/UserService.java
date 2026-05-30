package com.projects.accountservice.service.impl;


import com.projects.accountservice.entity.User;
import com.projects.accountservice.repository.UserRepository;
import com.projects.commonlib.security.JwtUserPrinciple;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new JwtUserPrinciple(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );    }
}
