package com.example.prj1be20231109.domain;
import com.example.prj1be20231109.utill.AppUtill;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Board {
    private Integer id;
    private String title;
    private String content;
    private String writer;
    private String nickName;
    private LocalDateTime inserted;
    private Integer countComment;
    private Integer countLike;
    private Integer countFile;
    private List<BoardFile> files;

    public String getAgo() {
        return AppUtill.getAgo(inserted, LocalDateTime.now());
    }


}
