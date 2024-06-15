package com.sparta.newsfeed.controller;


import com.sparta.newsfeed.dto.email.EmailRequestDto;
import com.sparta.newsfeed.dto.email.ReVerifyEMailRequestDto;
import com.sparta.newsfeed.dto.user.LoginUpRequestDto;
import com.sparta.newsfeed.dto.user.SignUpRequestDto;
import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserResponseDto;

import com.sparta.newsfeed.service.SignUpService;
import com.sparta.newsfeed.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final SignUpService signUpService;

    // 회원가입
    @PostMapping("/user/sign")
    @Operation(summary = "회원 가입", tags = {"사용자"})
    public String addUser(@Valid @RequestBody SignUpRequestDto requestDto) {
        return signUpService.addUser(requestDto);
    }

    // 이메일 인증
    @PostMapping("/user/verify")
    @Operation(summary = "이메일 인증", tags = {"사용자"})
    public String verifyEmail(@Valid @RequestBody EmailRequestDto requestDto) {
        signUpService.verifyEmail(requestDto);
        return "이메일 인증 성공";
    }

    // 이메일 인증 시간 초과시 인증번호 재요청 api
    @PostMapping("/user/reverify")
    @Operation(summary = "이메일 인증 재요청", tags = {"사용자"})
    public String reverifyEmail(@Valid @RequestBody ReVerifyEMailRequestDto requestDto) {
        return signUpService.reverifyEmail(requestDto);
    }

    // 로그인
    @PostMapping("/user/login")
    @Operation(summary = "로그인", tags = {"사용자"})
    public String loginUser(@Valid @RequestBody LoginUpRequestDto requestDto, HttpServletResponse response) {
        return signUpService.loginUser(requestDto, response);
    }

    // 로그아웃
    @PostMapping("/user/logout")
    @Operation(summary = "로그아웃", tags = {"사용자"})
    public String logoutUser(/*@RequestHeader("AccessToken") String accessToken,*/ HttpServletRequest request, HttpServletResponse response) {
        return signUpService.logoutUser(request, response);
    }

    // 회원 탈퇴
    @PostMapping("/user/delete")
    @Operation(summary = "회원 탈퇴", tags = {"사용자"})
    public String deleteUser(/*@RequestHeader("AccessToken") String accessToken,*/
            @RequestBody
            LoginUpRequestDto loginUpRequestDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        return signUpService.deleteUser(loginUpRequestDto, request, response);
    }

    // 유저 프로필 가져오기
    @GetMapping("/user/profile")
    @Operation(summary = "유저 프로필 가져오기", tags = {"사용자"})
    public UserResponseDto getUserProfile(HttpServletRequest request) {
        return userService.getUserProfile(request);
    }

    // 유저 프로필 수정
    @PatchMapping("/user/profile")
    @Operation(summary = "유저 프로필 수정", tags = {"사용자"})
    public String updateUserProfile(HttpServletRequest request, @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUserProfile(request, userRequestDto);
    }

    // 유저 프로필 사진
    @PatchMapping("/user/profile/m")
    @Operation(summary = "유저 프로필 사진 넣기", tags = {"사용자"})
    @Parameter(name = "Pictur", description = "사용자 사진")
    public String PictureUserProfile(HttpServletRequest servletRequest, @RequestPart MultipartFile Pictur) {
        return userService.PictureUserProfile(servletRequest, Pictur);
    }

}