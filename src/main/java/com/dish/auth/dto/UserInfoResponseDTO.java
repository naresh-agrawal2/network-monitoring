package com.dish.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponseDTO {

    private String token;
    private String username;
    private Integer user_id;
    private String email;
    private int roleId;
    private String roleName;
    private String roleList;
    private String nextPage;
}
