package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.dto.follow.FollowRequestDto;
import com.sparta.newsfeed.dto.follow.FollowResponseDto;
import com.sparta.newsfeed.dto.follow.FollowStatusResponseDto;
import com.sparta.newsfeed.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{followeeId}")
    @Operation(summary = "팔로우", tags = {"팔로우"})
    public FollowResponseDto followUser(@PathVariable Long followeeId, HttpServletRequest request) {
        FollowRequestDto requestDto = new FollowRequestDto(followeeId);
        return followService.followUser(requestDto, request);
    }

    @DeleteMapping("/follow/{followeeId}")
    @Operation(summary = "언팔로우", tags = {"팔로우"})
    public FollowResponseDto unfollowUser(@PathVariable Long followeeId, HttpServletRequest request) {
        FollowRequestDto requestDto = new FollowRequestDto(followeeId);
        return followService.unfollowUser(requestDto, request);
    }

    @GetMapping("/follow/status/{followeeId}")
    @Operation(summary = "팔로우 상태 확인", tags = {"팔로우"})
    public FollowStatusResponseDto checkFollowStatus(@PathVariable Long followeeId, HttpServletRequest request) {
        return followService.checkFollowStatus(followeeId, request);
    }
}
