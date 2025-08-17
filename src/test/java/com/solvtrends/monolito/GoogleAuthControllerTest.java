package com.solvtrends.monolito;

import com.solvtrends.monolito.controller.GoogleAuthController;
import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.security.JwtUtil;
import com.solvtrends.monolito.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleAuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private GoogleAuthController controller;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(controller, "clientId", "925724527987-o7delamuh52fjrvlkt514kr042hhehfc.apps.googleusercontent.com");
        ReflectionTestUtils.setField(controller, "clientSecret", "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");
        ReflectionTestUtils.setField(controller, "redirectUri", "http://localhost:8080/auth/google/callback");
    }

    @Test
    void googleCallback_DeveRetornarJwtEUsuario() {
//        RestTemplate restTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(controller, "restTemplate", restTemplate);

        Map tokenMap = Map.of("access_token", "google-token-123");
        ResponseEntity<Map> tokenResponse = new ResponseEntity<>(tokenMap, HttpStatus.OK);

        Map userMap = Map.of(
                "email", "teste@google.com",
                "given_name", "Google",
                "family_name", "User"
        );
        ResponseEntity<Map> userResponse = new ResponseEntity<>(userMap, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), ArgumentMatchers.<Class<Map>>any())).thenReturn(tokenResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), ArgumentMatchers.<Class<Map>>any())).thenReturn(userResponse);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("teste@google.com");
        usuarioMock.setNome("Google");
        usuarioMock.setSobrenome("User");
        when(userService.loginOrCreateUsuarioFromGoogle(anyString(), anyString(), anyString())).thenReturn(usuarioMock);
        when(jwtUtil.generateToken(anyString())).thenReturn("jwt-google-123");

        ResponseEntity<?> response = controller.googleCallback("code-google-123");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map body = (Map) response.getBody();
        assertEquals("jwt-google-123", body.get("jwt"));
        assertEquals("google-token-123", body.get("googleAccessToken"));
        assertNotNull(body.get("usuario"));
    }
}
