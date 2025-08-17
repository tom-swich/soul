package com.solvtrends.monolito.controller;

import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.security.JwtUtil;
import com.solvtrends.monolito.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@RestController
public class GoogleAuthController {

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    public GoogleAuthController(UserService userService, JwtUtil jwtUtil ,RestTemplate restTemplate) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    // 1. Redireciona para o Google
    @GetMapping("/auth/google/login")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=openid%20email%20profile"
                + "&access_type=offline"
                + "&prompt=consent";
        response.sendRedirect(url);
    }

    // 2. Callback do Google
    @GetMapping("/auth/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam String code) {
        // Troca code por access_token
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, request, Map.class);

        String accessToken = tokenResponse.getBody().get("access_token").toString();

        // Busca os dados do usuário
        HttpHeaders headersUser = new HttpHeaders();
        headersUser.setBearerAuth(accessToken);
        HttpEntity<?> userRequest = new HttpEntity<>(headersUser);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                userRequest,
                Map.class
        );
        Map userData = userInfoResponse.getBody();

        String email = userData.get("email") != null ? userData.get("email").toString() : "";
        String nome = userData.get("given_name") != null ? userData.get("given_name").toString() : "";
        String sobrenome = userData.get("family_name") != null ? userData.get("family_name").toString() : "";

        // Crie/recupere usuário na sua base
        Usuario usuario = userService.loginOrCreateUsuarioFromGoogle(email, nome, sobrenome);

        // Gere JWT interno
        String jwt = jwtUtil.generateToken(usuario.getEmail());

        // Resposta para o frontend
        return ResponseEntity.ok(Map.of(
                "jwt", jwt,
                "usuario", usuario,
                "googleAccessToken", accessToken
        ));
    }
}