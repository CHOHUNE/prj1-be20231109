package com.example.prj1be20231109.domain;


import lombok.Data;

import java.time.LocalDate;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private String writer;
    private LocalDate inserted;
    private String nickName;
}
