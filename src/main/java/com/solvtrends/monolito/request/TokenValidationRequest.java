package com.solvtrends.monolito.request;

import lombok.Data;

@Data
public class TokenValidationRequest {
    private String email;
    private int token;
}
