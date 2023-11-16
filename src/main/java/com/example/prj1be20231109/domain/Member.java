package com.example.prj1be20231109.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Member {
    private String id;
    private String password;
    private String email;
    private String nickName;
    private LocalDateTime inserted;
    private List<Auth> auth;

    public boolean isAdmin(){
        if(auth !=null){
            return auth.stream()
                    .map(a->a.getName())
                    .anyMatch(n->n.equals("admin"));

        }
        return false;
    }
}
