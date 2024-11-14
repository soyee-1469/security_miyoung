package com.example.security.springboot_security_miyoung.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {

    // 쿠키 추가
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // JavaScript로 접근 불가
        cookie.setSecure(true);   // HTTPS에서만 사용
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    // 쿠키 삭제
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setMaxAge(0);
                cookie.setValue("");
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }
    }

    // 쿠키 직렬화
    public static String serialize(Object obj) {
        return Base64.getEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키 역직렬화
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] decodedBytes = Base64.getUrlDecoder().decode(cookie.getValue());
            // JSON 문자열로 디코딩된 바이트를 역직렬화
            return objectMapper.readValue(decodedBytes, cls);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize cookie", e);
        }
    }

}
