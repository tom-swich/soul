package com.solvtrends.monolito.controller;

import com.solvtrends.monolito.model.TokenModel;
import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.request.TokenValidationRequest;
import com.solvtrends.monolito.response.ApiResponse;
import com.solvtrends.monolito.response.ResponseFactory;
import com.solvtrends.monolito.security.JwtUtil;
import com.solvtrends.monolito.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.solvtrends.monolito.service.UserDetailsService;
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
    private UserDetailsService userDetailsService;

    @GetMapping("/me")
    public ResponseEntity<Usuario> getUsuarioLogado(@RequestHeader("Authorization") String authHeader) throws ParseException {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.getUsernameFromToken(token);  // assume que o subject é o email

        Usuario usuario = userDetailsService.getMeByEmail(email);

        return ResponseEntity.ok(usuario);
    }


}
