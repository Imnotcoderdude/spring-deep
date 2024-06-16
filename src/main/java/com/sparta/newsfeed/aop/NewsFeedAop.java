package com.sparta.newsfeed.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class NewsFeedAop {

    // controller 테스트 전용 포인트컷
    @Pointcut("execution(* com.sparta.newsfeed.controller..*(..))")
    private void controllerTest() {}

    @Before("controllerTest()")
    public void logBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.info("Request URL : " + request.getRequestURL());
            log.info("HTTP 메서드 : " + request.getMethod());
            log.info("컨트롤러 : " + joinPoint.getSignature().getDeclaringTypeName() + " 메서드 : " +joinPoint.getSignature().getName());
            log.info("Request Params : " + request.getQueryString());
        }
    }
}
