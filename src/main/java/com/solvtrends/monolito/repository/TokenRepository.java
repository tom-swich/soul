package com.solvtrends.monolito.repository;

import com.solvtrends.monolito.model.TokenModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenModel, Long> {
        Optional<TokenModel> findByEmail(String email);
        Optional<TokenModel> findByUserId(String userId);
        Optional<TokenModel> findByEmailAndToken(String email, int token);
}
