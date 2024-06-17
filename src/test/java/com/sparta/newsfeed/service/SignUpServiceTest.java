package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.user.SignUpRequestDto;
import com.sparta.newsfeed.entity.Users.User;
import com.sparta.newsfeed.repository.EmailVerificationRepository;
import com.sparta.newsfeed.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    BCryptPasswordEncoder passwordEncoder;
    @Mock
    EmailService emailService;
    @Mock
    EmailVerificationRepository emailVerificationRepository;

    @InjectMocks
    private SignUpService signUpService;

    private SignUpRequestDto signUpRequestDto;

    @BeforeEach
    void setUp() {
        signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUserId("testUser1234");
        signUpRequestDto.setUsername("Test User");
        signUpRequestDto.setPassword("TestPassword123!");
        signUpRequestDto.setEmail("test@example.com");
        signUpRequestDto.setOne_liner("한줄 소개 테스트");
    }


    @Test
    void addUser() throws MessagingException {
        // give
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // when
        String result = signUpService.addUser(signUpRequestDto);

        // then
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        assertEquals("test@example.com 로 발송된 인증코드를 확인해주세요.", result);
    }

    @Test
    void reverifyEmail() {
    }

    @Test
    void verifyEmail() {
    }

    @Test
    void loginUser() {
    }

    @Test
    void logoutUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getUserRepository() {
    }

    @Test
    void getPasswordEncoder() {
    }

    @Test
    void getValidator() {
    }

    @Test
    void getJwtTokenProvider() {
    }

    @Test
    void getEmailService() {
    }

    @Test
    void getEmailVerificationRepository() {
    }

    @Test
    void getRequestDto() {
    }

    @Test
    void getLogger() {
    }
}