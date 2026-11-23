package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Member {

    private Long id;
    private Long teamId;
    private String name;
    private String role;
    private Integer number;
    private Integer courtPosition;
    private LocalDateTime createdAt;

    private String teamName;
}
