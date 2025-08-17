package com.solvtrends.monolito.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Token")
public class TokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="usuario_id")
    private long userId;
    private String email;
    private int token;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataCriacao;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataExpiracao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.dataExpiracao = LocalDateTime.now().plusMinutes(5);
    }

}