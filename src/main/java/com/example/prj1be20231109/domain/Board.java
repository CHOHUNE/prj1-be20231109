package com.example.prj1be20231109.domain;

import com.example.prj1be20231109.util.AppUtill;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime inserted;
    private String nickName;
    private Integer countComment;
    private Integer countLike;

    public String getAgo(){
        return AppUtill.getAgo(inserted,LocalDateTime.now());
    }

}


