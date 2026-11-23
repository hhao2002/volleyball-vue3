package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Application {

    private Long id;
    private Long userId;
    private String type;
    private String reason;
    private String status;
    private Long handlerId;
    private String handleRemark;
    private LocalDateTime createdAt;
    private LocalDateTime handledAt;

    private String applicantUsername;
    private String applicantNickname;
    private String handlerName;
}
