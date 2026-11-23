package cn.edu.hnzj.volleyball.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUser {

    private Long id;
    private String username;
    private String nickname;
    private String role;
}
