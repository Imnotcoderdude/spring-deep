package com.sparta.newsfeed.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SignUpRequestDtoTest {

    private final Validator validator;

    public SignUpRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSignUpRequestDto() {
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setUserId("testUserTest");
        requestDto.setUsername("Test User");
        requestDto.setPassword("TestPassword1!");
        requestDto.setEmail("test@example.com");
        requestDto.setOne_liner("oneLinerTest");

        assertEquals("testUserTest", requestDto.getUserId());
        assertEquals("Test User", requestDto.getUsername());
        assertEquals("TestPassword1!", requestDto.getPassword());
        assertEquals("test@example.com", requestDto.getEmail());
        assertEquals("oneLinerTest", requestDto.getOne_liner());
    }

}
