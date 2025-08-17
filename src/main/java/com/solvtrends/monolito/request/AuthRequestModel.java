package com.solvtrends.monolito.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class AuthRequestModel {

    @Email
    private String email;
    private String password;

}