package com.projects.accountservice.service.impl;

import com.projects.accountservice.dto.auth.AuthResponse;
import com.projects.accountservice.dto.auth.LoginRequest;
import com.projects.accountservice.dto.auth.SignupRequest;
import com.projects.accountservice.mapper.UserMapper;
import com.projects.accountservice.repository.UserRepository;
import com.projects.accountservice.service.AuthService;
import com.projects.commonlib.error.BadRequestException;
import com.projects.commonlib.security.AuthUtil;
import com.projects.commonlib.security.JwtUserPrinciple;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;

    @Override
    public AuthResponse signup(SignupRequest request) {
        userRepository.findByUsername(request.username())
                .ifPresent(user -> {
                    throw new BadRequestException("User already exists with the username:" + request.username());});

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        var jwtUserPrinciple = new JwtUserPrinciple(user.getId(), user.getName(), user.getUsername(), null, new ArrayList<>());

        var token = authUtil.generateAccessToken(jwtUserPrinciple);
        return new AuthResponse(token, userMapper.toUserProfileResponse(jwtUserPrinciple));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        var user = (JwtUserPrinciple) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }


}
