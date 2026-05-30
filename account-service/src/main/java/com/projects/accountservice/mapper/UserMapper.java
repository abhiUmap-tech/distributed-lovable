package com.projects.accountservice.mapper;

import com.projects.accountservice.dto.auth.SignupRequest;
import com.projects.accountservice.dto.auth.UserProfileResponse;
import com.projects.accountservice.entity.User;
import com.projects.commonlib.dto.UserDto;
import com.projects.commonlib.security.JwtUserPrinciple;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(SignupRequest signupRequest);

    @Mapping(source = "userId", target = "id")
    UserProfileResponse toUserProfileResponse(JwtUserPrinciple user);

    UserDto toUserDto(User user);
}
