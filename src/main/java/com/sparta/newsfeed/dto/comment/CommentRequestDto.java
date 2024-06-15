package com.sparta.newsfeed.dto.comment;


import lombok.Getter;

@Getter
public class CommentRequestDto {

    private Long id;
    private Long user_id;
    private String contents;

}
