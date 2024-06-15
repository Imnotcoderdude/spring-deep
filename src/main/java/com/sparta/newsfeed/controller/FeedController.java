package com.sparta.newsfeed.controller;

import com.sparta.newsfeed.dto.board.BoardResponseDto;
import com.sparta.newsfeed.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/feed")
    @Operation(summary = "피드 가져오기", tags = {"피드"})
    public List<BoardResponseDto> getFeed(HttpServletRequest request) {
        return feedService.getFeed(request);
    }
}
