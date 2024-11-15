
### README.md
![image](https://github.com/user-attachments/assets/f046ce22-5519-4c79-b4db-c6b840557d72)


## 권한에 따른 게시판 구현

- 사용자 역할에 따라 접근 권한을 제한
- JWT 토큰 기반의 인증을 사용

### 기본 설정

1. **role 기본값**: ROLE_USER
2. **회원가입 시 권한**: 모든 신규 회원은 기본적으로 ROLE_USER 권한을 가집니다.
3. **ROLE_ADMIN**: 모든 글을 조회
4. **ROLE_USER**: 본인이 작성한 글만 조회, 수정, 삭제 가능
5. **다른 사용자의 글 클릭 시**: access-denied.html 으로 이동

### 게시판 접근 규칙

- **비로그인 유저**: 로그인(/member/login) 페이지로 이동.
- **로그인 유저**: 로그인 후 게시판 목록 리스트로(/) 이동, 모든 글 목록 조회 가능
- **상세 페이지(/member/detail/{id}) 접근**: 
  - 본인이 작성한 글이거나 ROLE_ADMIN 권한이 있는 경우에 상세 페이지 접근 가능
  - 권한이 없을 경우 access-denied.html 페이지로 이동


### 작동순서
1. **JWT 토큰 생성 및 검증**  
   - `TokenProvider` 클래스에서 토큰의 생성, 검증, 정보 추출을 담당합니다.
   - **토큰 생성**: 사용자 정보와 만료 시간을 설정하여 JWT를 발급합니다.
   - **토큰 검증**: 유효한 토큰인지 검증하고 만료 시 `2`, 유효하지 않은 경우 `3`을 반환합니다.
```java
    public int validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("Token validated");
            return 1;
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            log.info("Token is expired");
            return 2;
        } catch (Exception e) {
            // 복호화 과정에서 에러 발생
            log.info("Token is not valid");
            return 3;
        }
    }
```
2. **controller제어**  

- `@PreAuthorize` 어노테이션을 사용하여 ROLE_ADMIN 또는 글 작성자 본인만 상세 페이지를 볼 수 있도록 설정
- 예외 상황에 따른 응답 코드와 페이지 이동이 처리됩니다.
- WebSecurityConfig에 반드시 @EnableMethodSecurity(prePostEnabled = true) 설정!

```java
@GetMapping("/{id}")
@PreAuthorize("hasRole('ROLE_ADMIN') or @boardService.isOwner(authentication.name, #id)")
public BoardDetailResponseDTO getBoardDetail(@PathVariable long id) {
    Board boardDetail = boardService.getBoardDetail(id);
    return BoardDetailResponseDTO.builder()
            .title(boardDetail.getTitle())
            .content(boardDetail.getContent())
            .created(boardDetail.getCreated())
            .userId(boardDetail.getUserId())
            .filePath(boardDetail.getFilePath())
            .build();
}
```

3. **WebSecurityConfig**
   - AccessDeniedHandler(403)와 AuthenticationEntryPoint(401) 
   - JSON 메시지로 변환해서 받아와 권한별로 페이지 이동 처리

```java
@Bean
public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"Access Denied\", \"message\": \"You do not have permission to access this resource.\"}");
    };
}

@Bean
public AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, authException) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Authentication is required to access this resource.\"}");
    };
}
```

4. **에러 코드 받아 JavaScript에서 페이지 이동시키기**

   - **401 Unauthorized**: 로그인 페이지로 리디렉션됩니다.
   - **403 Forbidden**: 권한이 부족한 경우 접근 제한 페이지(`/access-denied`)로 이동합니다.
   - **그 외 오류**: 예기치 않은 오류가 발생 시 경고 메시지를 표시합니다.

```javascript
error: (xhr) => {
    if (xhr.status === 401) {
        alert("로그인이 필요합니다.");
        window.location.href = "/member/login";
    } else if (xhr.status === 403) {
        window.location.href = "/access-denied"; // 권한 부족 시 접근 금지 페이지로 이동
    } else {
        alert("예기치 않은 오류가 발생했습니다.");
    }
}
```
### admin접속(ROLE_ADMIN)

-리스트 확인
![image](https://github.com/user-attachments/assets/9c079c58-7f5f-4e6e-81cd-e7612a2ca84a)

-모든 USER 상세 내용 확인
![image](https://github.com/user-attachments/assets/b7269571-81c7-4cc2-bf23-10a5d72a12b2)
![image](https://github.com/user-attachments/assets/93082d4a-6037-4472-af04-c9011e717f15)

### USER2접속(ROLE_USER)
-모든 리스트 확인
![image](https://github.com/user-attachments/assets/4538eeac-14da-4c2b-923b-e1fb2e6ed375)

-본인글 확인가능(USER2)
![image](https://github.com/user-attachments/assets/e080934b-c11a-4ad2-a2da-b0b313ffe4f9)
-user3글 확인시 접근불가
![image](https://github.com/user-attachments/assets/943599f5-61d5-4b11-9497-6c0f6eb17885)

---
