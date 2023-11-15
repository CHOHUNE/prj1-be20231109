package com.example.prj1be20231109.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer boardId;
    private String memberId;
    private String comment;
    private LocalDateTime inserted;
}
