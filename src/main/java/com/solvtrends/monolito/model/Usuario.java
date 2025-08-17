package com.solvtrends.monolito.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String nome;
    private String sobrenome;
    private String cpf;
    private String paisTelefone;
    private long telefone;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataCriacao;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataAtualizacao;

    private Boolean contaAtiva = true;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataCancelamento;




    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataExpiracao;





}
