package com.solvtrends.monolito;

import com.solvtrends.monolito.controller.MercadoLivreAuthController;
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

class MercadoLivreAuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MercadoLivreAuthController controller;

    @Mock
    private RestTemplate restTemplate;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(controller, "clientId", "8078697487965857");
        ReflectionTestUtils.setField(controller, "clientSecret", "jHU6hlkX3rygCBxRfqkkjw0Y90D9DCRI");
        ReflectionTestUtils.setField(controller, "redirectUri", "http://localhost:8080/auth/mercadolivre/callback");
    }

    @Test
    void mercadoLivreCallback_DeveRetornarJwtEUsuario() {
        // Simula token response
        RestTemplate restTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(controller, "restTemplate", restTemplate);

        Map tokenMap = Map.of("access_token", "ml-token-123");
        ResponseEntity<Map> tokenResponse = new ResponseEntity<>(tokenMap, HttpStatus.OK);

        Map userMap = Map.of(
                "email", "teste@ml.com",
                "first_name", "Teste",
                "last_name", "ML"
        );
        ResponseEntity<Map> userResponse = new ResponseEntity<>(userMap, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), ArgumentMatchers.<Class<Map>>any())).thenReturn(tokenResponse);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), ArgumentMatchers.<Class<Map>>any())).thenReturn(userResponse);

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("teste@ml.com");
        usuarioMock.setNome("Teste");
        usuarioMock.setSobrenome("ML");
        when(userService.loginOrCreateUsuarioFromMercadoLivre(anyString(), anyString(), anyString())).thenReturn(usuarioMock);
        when(jwtUtil.generateToken(anyString())).thenReturn("jwt-ml-123");

        ResponseEntity<?> response = controller.mercadoLivreCallback("code-ml-123");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map body = (Map) response.getBody();
        assertEquals("jwt-ml-123", body.get("jwt"));
        assertEquals("ml-token-123", body.get("mercadoLivreAccessToken"));
        assertNotNull(body.get("usuario"));
    }
}