package com.example.security.springboot_security_miyoung.model;


import com.example.security.springboot_security_miyoung.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Member {
    private long id;
    private String userId;
    private String password;
    private String userName;
    private Role role;



}
