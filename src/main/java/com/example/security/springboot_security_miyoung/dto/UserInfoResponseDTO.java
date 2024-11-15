package com.example.security.springboot_security_miyoung.dto;


import com.example.security.springboot_security_miyoung.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDTO {
    private long id;
    private String userId;
    private String userName;
    private Role role;

}
