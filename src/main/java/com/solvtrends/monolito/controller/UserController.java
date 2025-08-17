package com.solvtrends.monolito.controller;

import com.solvtrends.monolito.model.ChangePasswordDTO;
import com.solvtrends.monolito.model.TokenModel;
import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.repository.UserRepository;
import com.solvtrends.monolito.request.AuthRequestModel;
import com.solvtrends.monolito.request.PasswordResetRequest;
import com.solvtrends.monolito.response.ResponseFactory;
import com.solvtrends.monolito.security.JwtUtil;
import com.solvtrends.monolito.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Usuario> getUsuarioLogado(@RequestHeader("Authorization") String authHeader) throws ParseException {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getUsernameFromToken(token);
        Usuario usuario = userService.getMeByEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader, @RequestBody ChangePasswordDTO passwords) throws ParseException {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getUsernameFromToken(token);
        userService.changePassword( email, passwords.getOldPassword(), passwords.getNewPassword());
        return ResponseEntity.ok("Senha alterada com sucesso");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        userService.resetUserPassword(request.getEmail(), request.getToken(), request.getNewPassword());
        return ResponseFactory.ok("Senha atualizada com sucesso!", null);
    }

    @PostMapping("/reset-token")
    public ResponseEntity<?> resetPasswordEmail(@RequestParam String email) {
        TokenModel tokenModel = userService.resetPassword(email);
        return ResponseFactory.ok("Token gerado com sucesso", Map.of("email", tokenModel.getEmail()));
    }

    @DeleteMapping("/cancel-account")
    public ResponseEntity<?> cancelAccount(@RequestHeader("Authorization") String authHeader) throws ParseException {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getUsernameFromToken(token);
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
        userService.cancelarConta(usuario);
        return ResponseEntity.ok("Conta cancelada com sucesso");
    }
}
