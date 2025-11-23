package com.habitus.apipi.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String email;
    private String oldPassword;
    private String newPassword;
}
