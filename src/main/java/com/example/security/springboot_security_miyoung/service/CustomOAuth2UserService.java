package com.example.security.springboot_security_miyoung.service;

import com.example.security.springboot_security_miyoung.config.jwt.TokenProvider;
import com.example.security.springboot_security_miyoung.enums.Role;
import com.example.security.springboot_security_miyoung.mapper.MemberMapper;
import com.example.security.springboot_security_miyoung.model.Member;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final TokenProvider tokenProvider;
    private final MemberMapper memberMapper;
    private final RestTemplate restTemplate;

    public CustomOAuth2UserService(TokenProvider tokenProvider, MemberMapper memberMapper) {
        this.tokenProvider = tokenProvider;
        this.memberMapper = memberMapper;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Kakao 계정 정보 추출
        String userId = oAuth2User.getAttribute("id").toString();
        Map<String, Object> properties = (Map<String, Object>) oAuth2User.getAttribute("properties");
        String nickname = properties != null ? properties.get("nickname").toString() : "Unknown";

        // 사용자 생성 또는 조회
        Member member = getOrCreateUser(userId, nickname);

        // JWT 토큰 생성
        String jwtToken = tokenProvider.generateToken(member, Duration.ofHours(1));

        // OAuth2User 속성에 토큰 추가
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("token", jwtToken);

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())),
                attributes,
                "id"
        );
    }

    public String processKakaoToken(String kakaoAccessToken) {
        // Kakao API에서 사용자 정보 가져오기
        Map<String, Object> userInfo = getUserInfoFromKakao(kakaoAccessToken);

        // 사용자 정보 추출
        String userId = userInfo.get("id").toString();
        String nickname = ((Map<String, String>) userInfo.get("properties")).get("nickname");

        // 사용자 생성 또는 조회
        Member member = getOrCreateUser(userId, nickname);

        // JWT 토큰 생성
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }

    private Map<String, Object> getUserInfoFromKakao(String kakaoAccessToken) {
        // Kakao API 호출
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user info from Kakao API", e);
        }
    }

    private Member getOrCreateUser(String userId, String nickname) {
        // DB에서 사용자 조회
        Member existingMember = memberMapper.findByUserId(userId);
        if (existingMember != null) {
            return existingMember;
        }

        // 신규 사용자 생성 및 저장
        Member newMember = Member.builder()
                .userId(userId)
                .userName(nickname)
                .password("") // OAuth 사용자는 비밀번호를 저장하지 않음
                .role(Role.ROLE_USER)
                .build();

        memberMapper.signUp(newMember);
        return newMember;
    }
}
