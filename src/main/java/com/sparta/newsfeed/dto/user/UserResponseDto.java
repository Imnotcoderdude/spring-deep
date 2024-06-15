package com.sparta.newsfeed.dto.user;

import com.sparta.newsfeed.entity.Users.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private final Long id;
    private final String username;
    private final String email;
    private final String One_liner;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.One_liner = user.getOne_liner();
    }
}