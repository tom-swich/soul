package com.solvtrends.monolito.repository;

import com.solvtrends.monolito.model.TokenModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenModel, Long> {
        Optional<TokenModel> findByEmail(String email);
        Optional<TokenModel> findByUserId(long userId);
        Optional<TokenModel> findByEmailAndToken(String email, int token);
}
