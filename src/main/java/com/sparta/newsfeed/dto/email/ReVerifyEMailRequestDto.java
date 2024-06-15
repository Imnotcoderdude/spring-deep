package com.sparta.newsfeed.dto.email;

import lombok.Getter;

@Getter
public class ReVerifyEMailRequestDto {
    private String email;
    private String password;
}