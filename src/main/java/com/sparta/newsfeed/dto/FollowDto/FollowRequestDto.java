package com.sparta.newsfeed.dto.FollowDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowRequestDto {
    private Long followeeId;

    public FollowRequestDto(Long followeeId) {
        this.followeeId = followeeId;
    }
}