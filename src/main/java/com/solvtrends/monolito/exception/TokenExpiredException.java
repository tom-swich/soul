package com.solvtrends.monolito.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token inválido ou expirado");
    }
}
