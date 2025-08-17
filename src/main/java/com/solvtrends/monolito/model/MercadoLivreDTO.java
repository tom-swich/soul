package com.solvtrends.monolito.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MercadoLivreDTO {

    private String accessToken;
    private String refreshToken;
    private LocalDateTime tokenExpiraEm;

}
