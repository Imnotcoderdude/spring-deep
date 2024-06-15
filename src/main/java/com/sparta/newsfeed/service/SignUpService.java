package com.sparta.newsfeed.service;

import com.sparta.newsfeed.dto.email.EmailRequestDto;
import com.sparta.newsfeed.dto.email.ReVerifyEMailRequestDto;
import com.sparta.newsfeed.dto.user.LoginUpRequestDto;
import com.sparta.newsfeed.dto.user.SignUpRequestDto;
import com.sparta.newsfeed.entity.EmailVerification;
import com.sparta.newsfeed.entity.Users.User;
import com.sparta.newsfeed.entity.Users.UserStatus;
import com.sparta.newsfeed.jwt.util.JwtTokenProvider;
import com.sparta.newsfeed.repository.EmailVerificationRepository;
import com.sparta.newsfeed.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Logger;

@Service
@Getter
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;
    private SignUpRequestDto requestDto;

    private final Logger logger = Logger.getLogger(SignUpService.class.getName());

    // 유저 회원가입
    public String addUser(SignUpRequestDto requestDto) {
        this.requestDto = requestDto;
        User existingUser = userRepository.findByUserId(requestDto.getUserId());
        if (existingUser != null) {
            if (existingUser.getUserStatus() == UserStatus.ACTIVE) {
                throw new IllegalArgumentException("중복된 사용자 ID입니다.");
            } else if (existingUser.getUserStatus() == UserStatus.WITHDRAWAL) {
                throw new IllegalArgumentException("탈퇴한 사용자 ID입니다.");
            }
        }

        User user = new User(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setUserStatus(UserStatus.WAIT_EMAIL); // 이메일 인증을 하기 전에는 이메일 인증을 기다리는 상태코드.
        user.setSend_email_time(LocalDateTime.now()); // 이메일 발송 시간 기록.

        // 인증코드 생성하여 이메일 전송
        String code = generateVerificationCode();
        EmailVerification emailVerification = new EmailVerification(requestDto.getEmail(), code);
        emailVerificationRepository.save(emailVerification);
        sendVerificationEmail(requestDto.getEmail(), code);

        userRepository.save(user);
        return requestDto.getEmail() + " 로 발송된 인증코드를 확인해주세요.";
    }

    // 이메일 인증시 보낼 인증 코드
    private String generateVerificationCode() {
        // 랜덤 돌리기
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // 이메일 발송 양식
    private void sendVerificationEmail(String email, String code) {
        String subject = "이메일 인증 코드";
        String text = "인증 코드 번호 : " + code;
        try {
            emailService.sendEmail(email, subject, text);
        } catch (Exception e) {
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    // 이메일 인증 시간 초과시 이메일 인증 재요청 메서드
    @Transactional
    public String reverifyEmail(ReVerifyEMailRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 찾을 수 없습니다" + requestDto.getEmail()));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("회원가입시 입력한 비밀번호와 일치하지 않습니다.");
        }

        // 새로운 인증코드 만들기
        String code = generateVerificationCode();

        // DB에 이메일 인증 정보 교체
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(requestDto.getEmail())
                .orElseGet(() -> new EmailVerification(requestDto.getEmail(), code));

        emailVerification.setCode(code);
        emailVerificationRepository.save(emailVerification);

        // 이메일 발송 시간초기화
        user.setSend_email_time(LocalDateTime.now());
        userRepository.save(user);

        // 인증 코드 이메일로 전송
        sendVerificationEmail(requestDto.getEmail(), code);

        return requestDto.getEmail() + "로 발송한 인증 코드를 확인해 주세요.";

    }

    // 이메일 검사 후 상태 변경.
    @Transactional
    public String verifyEmail(EmailRequestDto requestDto) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmailAndCode(requestDto.getEmail(), requestDto.getCode())
                .orElseThrow(() -> new IllegalArgumentException("인증 코드가 일치하지 않습니다."));

        emailVerification.setVerified(true);
        emailVerificationRepository.save(emailVerification);

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("유저 이메일이 올바르지 않습니다."));

        // 이메일 제한시간 추가.
        if (user.getSend_email_time().plusSeconds(180).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(user.getEmail() + "로 발송한 이메일의 제한 시간이 만료되었습니다.");
        }

        emailVerification.setVerified(true);
        emailVerificationRepository.save(emailVerification);

        // 유저 상태코드를 활성화로 변경
        user.setUserStatus(UserStatus.ACTIVE);
        // 변경된 상태코드를 유저 객체에 저장
        userRepository.save(user);

        return "이메일 : " + requestDto.getEmail() + " 님의 인증이 완료되었습니다.";
    }


    // 유저 로그인
    public String loginUser(LoginUpRequestDto requestDto, HttpServletResponse response) {
        User user = userRepository.findByUserId(requestDto.getUserId());

        userlogin(requestDto, user);

        if (user.getUserStatus() == UserStatus.WITHDRAWAL) {
            throw new IllegalArgumentException("이미 탈퇴한 사용자 입니다.");
        }

        if (user.getUserStatus() == UserStatus.WAIT_EMAIL) {
            throw new IllegalArgumentException("이메일 인증 확인이 되지 않습니다.");
        }

        // 로그인 시 액세스 토큰 및 리프레시 토큰 생성 및 저장
        String accessToken = jwtTokenProvider.generateToken(user.getUserId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        //토큰 자동 저장
        jwtTokenProvider.addToken(accessToken, response);
        user.setRefresh_token(refreshToken);
        userRepository.save(user);

        return "어서오세요 " + user.getUsername() + "님 로그인이 완료되었습니다";
    }

    private void userlogin(LoginUpRequestDto requestDto, User user) {
        logger.info("userlogin 메서드 작동");
        if (user == null) {
            logger.info("유저 아이디 미존재 에러");
            throw new IllegalArgumentException("유저 아이디가 존재하지 않습니다.");
        }

        if (!user.getUserId().equals(requestDto.getUserId())) {
            logger.info("아이디 불일치 에러");
            throw new IllegalArgumentException("유저 아이디가 일치하지 않습니다");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            logger.info("유저 비밀번호 불일치 에러");
            throw new IllegalArgumentException("유저 비밀번호가 올바르지 않습니다.");
        }
        logger.info("아이디와 비밀번호 검사 이상없음.");
    }

    // 로그아웃 메서드
    // 로그아웃시 리프레쉬 토큰 없애기.
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = jwtTokenProvider.getTokenUser(request);
            user.setRefresh_token(null); // 로그아웃시 리프레쉬 토큰 null 하기.
            userRepository.save(user);
            jwtTokenProvider.deleteCookie(response);
        } catch (Exception e) {
            logger.info("로그아웃 과정에서 예외 발생: " + e.getMessage());
            throw e;
        }
        return "로그아웃 완료";
    }

    // 회원 탈퇴 메서드
    @Transactional
    public String deleteUser(LoginUpRequestDto loginUpRequestDto,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        User user = jwtTokenProvider.getTokenUser(request);
        logger.info("회원 탈퇴 요청을 받았습니다: " + user.getUsername());

        // 아이디와 비밀번호 검증 로직 재사용
        userlogin(loginUpRequestDto, user);

        if (user.getUserStatus() == UserStatus.WITHDRAWAL) {
            throw new IllegalArgumentException("이미 탈퇴한 사용자입니다.");
        }

        user.setUserStatus(UserStatus.WITHDRAWAL);
        jwtTokenProvider.deleteCookie(response);
        userRepository.save(user);
        logger.info("사용자 " + user.getUsername() + "가 성공적으로 탈퇴되었습니다.");
        return "회원탈퇴가 완료되었습니다 " + user.getUsername() + "님\n 안녕을 기원합니다.";
    }
}