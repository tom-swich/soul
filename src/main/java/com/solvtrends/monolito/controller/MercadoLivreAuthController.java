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
public class MercadoLivreAuthController {

    @Value("${mercadolivre.client-id}")
    private String clientId;

    @Value("${mercadolivre.client-secret}")
    private String clientSecret;

    @Value("${mercadolivre.redirect-uri}")
    private String redirectUri;

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    public MercadoLivreAuthController(UserService userService, JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    // 1. Redireciona para o Mercado Livre
    @GetMapping("/auth/mercadolivre/login")
    public void redirectToMercadoLivre(HttpServletResponse response) throws IOException {
        String url = "https://auth.mercadolivre.com.br/authorization?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
        response.sendRedirect(url);
    }

    // 2. Callback do Mercado Livre
    @GetMapping("/auth/mercadolivre/callback")
    public ResponseEntity<?> mercadoLivreCallback(@RequestParam String code) {
        // Troca o code pelo access_token
//        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://api.mercadolibre.com/oauth/token";

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

        HttpHeaders headersUser = new HttpHeaders();
        headersUser.setBearerAuth(accessToken);
        HttpEntity<?> userRequest = new HttpEntity<>(headersUser);

        ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://api.mercadolibre.com/users/me",
                HttpMethod.GET,
                userRequest,
                Map.class
        );
        Map userData = userResponse.getBody();

        String email = userData.get("email") != null ? userData.get("email").toString() : userData.get("nickname").toString() + "@ml.com";
        String nome = userData.get("first_name") != null ? userData.get("first_name").toString() : "";
        String sobrenome = userData.get("last_name") != null ? userData.get("last_name").toString() : "";

        // Crie/recupere usuário na sua base
        Usuario usuario = userService.loginOrCreateUsuarioFromMercadoLivre(email, nome, sobrenome);

        // Gere JWT interno
        String jwt = jwtUtil.generateToken(usuario.getEmail());

        // Resposta para o frontend
        return ResponseEntity.ok(Map.of(
                "jwt", jwt,
                "usuario", usuario,
                "mercadoLivreAccessToken", accessToken
        ));
    }
}