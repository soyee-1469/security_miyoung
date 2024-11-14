package com.example.security.springboot_security_miyoung.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BoardController {

    @GetMapping("/")
    public String boardList() {
        return "board-list";
    }

    @GetMapping("/detail")
    public String detail(
            @RequestParam("id") Long id,
            Model model
    ) {
        model.addAttribute("id", id);
        return "board-detail";
    }

    @GetMapping("/write")
    public String write() {
        return "board-write";
    }

    @GetMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("id", id);
        return "board-update";
    }

}
