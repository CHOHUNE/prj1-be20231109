package com.example.prj1be20231109.controller;

import com.example.prj1be20231109.domain.Comment;
import com.example.prj1be20231109.domain.Member;
import com.example.prj1be20231109.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Comment comment,
                              @SessionAttribute(value = "login", required = false) Member login) {

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (service.validate(comment)) {
            if (service.add(comment, login)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }

        } else {
            return ResponseEntity.badRequest().build();
        }

        }
    @GetMapping("list")
    public List<Comment> list(@RequestParam("id") Integer boardId){
        return service.list(boardId);
    }
}
