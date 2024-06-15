package com.sparta.newsfeed.entity.Users;

import com.sparta.newsfeed.dto.user.SignUpRequestDto;
import com.sparta.newsfeed.dto.user.UserRequestDto;
import com.sparta.newsfeed.entity.Board;
import com.sparta.newsfeed.entity.Comment;
import com.sparta.newsfeed.entity.Likes.ContentsLike;
import com.sparta.newsfeed.entity.Timer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Setter
@Entity
@Getter
public class User extends Timer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //아이디 비번
    @Column(unique = true)
    private String userId;
    // 비밀번호
    private String password;
    //이름
    private String username;
    //사진 url
    private String PicturUrl;
    //이매일
    private String email;
    //한줄 소개
    private String one_liner;
    //리프레쉬 토큰
    private String refresh_token;

    // 이메일 발송 시간 기록.
    private LocalDateTime send_email_time;

    // 유저의 상태코드. 기본값은 정상임.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus userStatus = UserStatus.ACTIVE;

    //개시판
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "user_id")
    private List<Board> boardList;

    // 댓글
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "user_id")
    private List<Comment> commentList;

    // 좋아요
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "user_id")
    private List<ContentsLike> contentsLikeList;


    public User() {
    }

    public User(SignUpRequestDto userSignRequestDto) {
        this.userId = userSignRequestDto.getUserId();
        this.password = userSignRequestDto.getPassword();
        this.username = userSignRequestDto.getUsername();
        this.email = userSignRequestDto.getEmail();
        this.one_liner = userSignRequestDto.getOne_liner();
        this.userStatus = userSignRequestDto.getUserStatus();
    }

    public void update(UserRequestDto userSignRequestDto) {
        this.username = userSignRequestDto.getUsername();
        this.email = userSignRequestDto.getEmail();
        this.one_liner = userSignRequestDto.getOne_liner();
    }
}