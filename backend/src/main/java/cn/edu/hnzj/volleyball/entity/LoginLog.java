package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginLog {

    private Long id;
    private Long userId;
    private String username;
    private String ip;
    private String userAgent;
    private Integer success;
    private String message;
    private LocalDateTime loginTime;
}
