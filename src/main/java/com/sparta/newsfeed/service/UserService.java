package com.sparta.newsfeed.service;


import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.dto.user.UserResponseDto;
import com.sparta.newsfeed.entity.Users.User;
import com.sparta.newsfeed.jwt.util.JwtTokenProvider;
import com.sparta.newsfeed.repository.MultimediaRepository;
import com.sparta.newsfeed.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MultimediaRepository MultimediaRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    // 유저 프로필 가져오기
    public UserResponseDto getUserProfile(HttpServletRequest request) {
        User user = jwtTokenProvider.getTokenUser(request);
        return new UserResponseDto(user);
    }

    // 프로필 변경
    @Transactional
    public String updateUserProfile(HttpServletRequest request, UserRequestDto userRequestDto) {
        User user = jwtTokenProvider.getTokenUser(request);
        // 비밀번호 변경 여부 확인 및 처리
        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            // 현재 비밀번호 확인
            if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("유저 비밀번호가 올바르지 않습니다.");
            }

            // 새 비밀번호가 현재 비밀번호와 같은지 확인
            if (passwordEncoder.matches(userRequestDto.getNewpassword(), user.getPassword())) {
                throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 동일할 수 없습니다.");
            }

            // 새 비밀번호를 설정
            //아 이거 new 페스워드가 아니라 기존 패스워드 가져온거네
            String newPassword = userRequestDto.getNewpassword();
            // 비밀번호 형식이 올바르지 않은 경우 예외 처리
            if (!isValidPasswordFormat(newPassword)) {
                throw new IllegalArgumentException("올바르지 않은 비밀번호 형식입니다");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            logger.info("비밀번호 변경 완료");
        }
        if (userRequestDto.getEmail() != null) user.setEmail(userRequestDto.getEmail());
        if (userRequestDto.getUsername() != null) user.setUsername(userRequestDto.getUsername());
        if (userRequestDto.getOne_liner() != null) user.setOne_liner(userRequestDto.getOne_liner());


        return "수정완료 'Get' 으로 확인해 주세요";
    }

    // 비밀번호 형식이 올바른지 확인하는 메서드
    private boolean isValidPasswordFormat(String password) {
        // 비밀번호가 8자 이상, 숫자와 영문자를 혼합하여 구성되어 있는지 확인
        String regex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }

    // 프로필 사진 올리기
    @Transactional
    public String PictureUserProfile(HttpServletRequest servletRequest, MultipartFile Pictur) {
        try {
            User user = jwtTokenProvider.getTokenUser(servletRequest);
            if (Pictur != null && !Pictur.isEmpty() && Pictur.getContentType() != null && Pictur.getContentType().toLowerCase().contains("image")) {
                String PicturKey = "Pictur/" + UUID.randomUUID();
                uploadFileToS3(PicturKey, Pictur.getBytes(), Pictur.getContentType());
                user.setPicturUrl(getS3Url(PicturKey));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "프로필 사진 삽입완료";
    }

    // 저장 주소
    private String getS3Url(String key) {
        return "https://onebytenewsfeed.s3.amazonaws.com/" + key;
    }

    // s3
    private void uploadFileToS3(String key, byte[] bytes, String contentType) {
        if (bytes.length > 10 * 1024 * 1024 && contentType.toLowerCase().contains("image")) {
            throw new IllegalArgumentException("이미지 용량이 너무 큽니다. 최대 10MB까지 업로드 가능합니다.");
        }

        try (S3Client s3Client = S3Client.builder().region(Region.AP_NORTHEAST_2).build()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("onebytenewsfeed")
                    .key(key)
                    .contentType(contentType)
                    .build();
            RequestBody requestBody = RequestBody.fromBytes(bytes);
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, requestBody);
        }
    }
}