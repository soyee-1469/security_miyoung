package com.example.security.springboot_security_miyoung.mapper;


import com.example.security.springboot_security_miyoung.model.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void signUp(Member member);
    Member findByUserId(String userId);
}
