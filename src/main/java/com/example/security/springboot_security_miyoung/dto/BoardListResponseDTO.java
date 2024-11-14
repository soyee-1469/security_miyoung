package com.example.security.springboot_security_miyoung.dto;


import com.example.security.springboot_security_miyoung.model.Board;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardListResponseDTO {
    List<Board> boards;
    boolean last;
}
