package com.sparta.newsfeed.dto.follow;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowResponseDto {
    private String message;

    public FollowResponseDto(String message) {
        this.message = message;
    }
}