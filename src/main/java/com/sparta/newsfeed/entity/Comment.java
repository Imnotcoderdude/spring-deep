package com.sparta.newsfeed.entity;

import com.sparta.newsfeed.dto.comment.CommentRequestDto;
import com.sparta.newsfeed.entity.Users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Entity
@Getter
@NoArgsConstructor

public class Comment extends Timer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long user_id;
    private Long board_user_id;
    private String contents;
    private Long Likecounts;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private Board board;

    public Comment(CommentRequestDto commentRequestDto, Board board, User user) {
        this.id = commentRequestDto.getId();
        this.user_id = user.getId();
        this.board_user_id = board.getUser_id();
        this.contents = commentRequestDto.getContents();
    }

    public void setLikecounts(Long likecounts) {
        this.Likecounts = likecounts;
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.contents = commentRequestDto.getContents();
    }
}