package com.solvtrends.monolito.controller;

import com.solvtrends.monolito.request.AuthRequestModel;
import com.solvtrends.monolito.model.TokenModel;
import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.request.PasswordResetRequest;
import com.solvtrends.monolito.request.TokenValidationRequest;
import com.solvtrends.monolito.response.ResponseFactory;
import com.solvtrends.monolito.security.JwtUtil;
import com.solvtrends.monolito.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario user) {
        Usuario novoUsuario = userService.register(user);
        return ResponseFactory.created("Usuário registrado com sucesso!", Map.of("email", novoUsuario.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestModel request) {
         Usuario usuario = userService.authenticate(request.getEmail(), request.getPassword());
         return  ResponseFactory.ok("Login realizado com sucesso", Map.of("accessToken", jwtUtil.generateToken(usuario.getEmail())));
    }



    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestBody TokenValidationRequest request) {
       userService.verifyResetToken(request.getEmail(), request.getToken());
       return ResponseFactory.ok("Token válido", null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        TokenModel tokenModel = userService.resetPassword(email);

        return ResponseFactory.ok("Token gerado com sucesso", Map.of("email", tokenModel.getEmail()));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        userService.resetUserPassword(request.getEmail(), request.getToken(), request.getNewPassword());
        return ResponseFactory.ok("Senha atualizada com sucesso!", null);

    }




}