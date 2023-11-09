package com.example.prj1be20231109.controller;

import com.example.prj1be20231109.domain.Board;
import com.example.prj1be20231109.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService service;

    @PostMapping("add")
    public ResponseEntity add(@RequestBody Board board) {
        if (!service.validate(board)) {
            return ResponseEntity.badRequest().build();
        }

        if (service.save(board)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}







