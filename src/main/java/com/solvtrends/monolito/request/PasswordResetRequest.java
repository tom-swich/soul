package com.solvtrends.monolito.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequest {

    @Email
    private String email;
    private int token;
    private String newPassword;
}

