package com.sparta.newsfeed.dto.board;

import com.sparta.newsfeed.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private final Long board_id;
    private final Long board_user_id;
    private final String board_contents;
    private final LocalDateTime created_time;
    private final LocalDateTime modified_time;
    private final Long Likecounts;
    private String message;

    public BoardResponseDto(Board board) {
        this.board_id = board.getId();
        this.board_user_id = board.getUser_id();
        this.board_contents = board.getContents();
        this.created_time = board.getCreatedTime();
        this.modified_time = board.getModifiedTime();
        this.Likecounts = board.getLikecounts();
        this.message = "";
    }

    public BoardResponseDto(Board board, long Likecounts) {
        this.board_id = board.getId();
        this.board_user_id = board.getUser_id();
        this.board_contents = board.getContents();
        this.created_time = board.getCreatedTime();
        this.modified_time = board.getModifiedTime();
        this.Likecounts = Likecounts;
    }

    public BoardResponseDto(Board board, long Likecounts, String message) {
        this.board_id = board.getId();
        this.board_user_id = board.getUser_id();
        this.board_contents = board.getContents();
        this.created_time = board.getCreatedTime();
        this.modified_time = board.getModifiedTime();
        this.Likecounts = Likecounts;
        this.message = message;
    }

}
