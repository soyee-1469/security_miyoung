package com.example.security.springboot_security_miyoung.mapper;


import com.example.security.springboot_security_miyoung.model.Board;
import com.example.security.springboot_security_miyoung.model.Paging;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<Board> selectBoardList(Paging page);
    int countBoards();
    Board selectBoardDetail(long id);
    void saveArticle(Board board);
    void deleteBoardById(long id);
    void updateArticle(Board board);
}
