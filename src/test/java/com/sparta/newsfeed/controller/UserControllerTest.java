package com.sparta.newsfeed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.newsfeed.config.SecurityConfig;
import com.sparta.newsfeed.dto.user.SignUpRequestDto;
import com.sparta.newsfeed.mvc.NewsFeedTestMvcFilter;
import com.sparta.newsfeed.service.SignUpService;
import com.sparta.newsfeed.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(SpringExtension.class)
@WebMvcTest(
        controllers = {UserController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private SignUpService signUpService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new NewsFeedTestMvcFilter()))
                .build();
    }

    @Test
    void addUser() throws Exception {
        // given
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setUserId("testUserTest");
        requestDto.setUsername("Test User");
        requestDto.setPassword("Password1!");
        requestDto.setEmail("test@example.com");
        requestDto.setOne_liner("Hello World!");

        String returnMessage = requestDto.getEmail() + " .";

        // Mock 설정: signUpService의 addUser 메서드가 호출되면 "expectedMessage"을 반환하도록 설정합니다.
        given(signUpService.addUser(any(SignUpRequestDto.class))).willReturn(returnMessage);

        // when & then
        String response = mockMvc.perform(post("/api/user/sign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(returnMessage, response);
    }


    @Test
    void verifyEmail() {
    }

    @Test
    void reverifyEmail() {
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
}
