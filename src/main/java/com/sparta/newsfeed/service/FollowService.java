package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.follow.FollowRequestDto;
import com.sparta.newsfeed.dto.follow.FollowResponseDto;
import com.sparta.newsfeed.dto.follow.FollowStatusResponseDto;
import com.sparta.newsfeed.entity.Follow;
import com.sparta.newsfeed.entity.Users.User;
import com.sparta.newsfeed.jwt.util.JwtTokenProvider;
import com.sparta.newsfeed.repository.FollowRepository;
import com.sparta.newsfeed.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public FollowResponseDto followUser(FollowRequestDto requestDto, HttpServletRequest request) {
        User follower = jwtTokenProvider.getTokenUser(request);
        User followee = userRepository.findById(requestDto.getFolloweeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (follower.getId().equals(followee.getId())) {
            return new FollowResponseDto("본인을 팔로우할 수 없습니다.");
        }

        if (followRepository.findByFollowerAndFollowee(follower, followee).isPresent()) {
            return new FollowResponseDto(followee.getUsername() + "을(를) 이미 팔로우하고 있습니다.");
        }

        Follow follow = new Follow(follower, followee);
        followRepository.save(follow);
        return new FollowResponseDto(followee.getUsername() + "을(를) 팔로우했습니다.");
    }

    @Transactional
    public FollowResponseDto unfollowUser(FollowRequestDto requestDto, HttpServletRequest request) {
        User follower = jwtTokenProvider.getTokenUser(request);
        User followee = userRepository.findById(requestDto.getFolloweeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new IllegalArgumentException(followee.getUsername() + "을(를) 팔로우하고 있지 않습니다."));

        followRepository.delete(follow);
        return new FollowResponseDto(followee.getUsername() + "을(를) 언팔로우했습니다.");
    }

    public FollowStatusResponseDto checkFollowStatus(Long followeeId, HttpServletRequest request) {
        User follower = jwtTokenProvider.getTokenUser(request);
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        boolean isFollowing = followRepository.findByFollowerAndFollowee(follower, followee).isPresent();
        String message = isFollowing ? follower.getUsername() + "은(는) " + followee.getUsername() + "을(를) 팔로우하고 있습니다."
                : follower.getUsername() + "은(는) " + followee.getUsername() + "을(를) 팔로우하고 있지 않습니다.";
        return new FollowStatusResponseDto(message);
    }
}
