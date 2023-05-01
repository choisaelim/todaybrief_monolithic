package com.example.user.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String accessToken;
    private String type = "Bearer";
    private Long id;
    private String userName;
    private String email;
    private List<String> roles;
    private String userId;

    public JwtResponse(String accessToken, Long id, String userName, String email, String userId, List<String> roles) {
        this.accessToken = accessToken;
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.roles = roles;
        this.userId = userId;
    }
}
