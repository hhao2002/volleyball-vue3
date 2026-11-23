package cn.edu.hnzj.volleyball.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Team {

    private Long id;
    private String name;
    private String coach;
    private String color;
    private Long createdBy;
    private LocalDateTime createdAt;

    private List<Member> members = new ArrayList<>();
    private boolean locked;
}
