package com.example.security.springboot_security_miyoung.controller;

import com.example.security.springboot_security_miyoung.service.CustomOAuth2UserService;
import com.example.security.springboot_security_miyoung.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OAuthController {

    private static final Logger log = LoggerFactory.getLogger(OAuthController.class);

    private final CustomOAuth2UserService customOAuth2UserService;

    public OAuthController(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @GetMapping("/oauth/kakao/success")
    public ResponseEntity<?> kakaoLoginSuccess(@RequestParam("token") String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        // Redirect to main page with JWT
        headers.add("Location", "/");
        return ResponseEntity.status(302).headers(headers).build();
    }

    @PostMapping("/api/kakao/login")
    public ResponseEntity<Map<String, String>> kakaoLogin(
            @RequestBody Map<String, String> request,
            HttpServletResponse response
    ) {
        String kakaoAccessToken = request.get("kakaoAccessToken");

        // Kakao Access Token 처리 및 사용자 정보 추출
        String jwtToken = customOAuth2UserService.processKakaoToken(kakaoAccessToken);

        // Refresh Token 쿠키 설정
        CookieUtil.addCookie(response, "refreshToken", jwtToken, 7 * 24 * 60 * 60);

        // 응답 구성
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("token", jwtToken);
        responseBody.put("redirectUrl", "/"); // 메인 페이지 경로

        return ResponseEntity.ok(responseBody);
    }


}
