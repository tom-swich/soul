package com.solvtrends.monolito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solvtrends.monolito.TestSecurityConfig;
import com.solvtrends.monolito.model.TokenModel;
import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.request.AuthRequestModel;
import com.solvtrends.monolito.request.TokenValidationRequest;
import com.solvtrends.monolito.security.JwtUtil;
import com.solvtrends.monolito.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.solvtrends.monolito.service.UserDetailsSecurityService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsSecurityService userDetailsSecurityService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerTest() throws Exception {
        Usuario user = new Usuario();
        user.setEmail("teste@teste.com");
        Mockito.when(userService.register(any(Usuario.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .with(csrf())) // <- adicione isto
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso!"));
    }

    @Test
    void loginTest() throws Exception {
        AuthRequestModel req = new AuthRequestModel();
        req.setEmail("teste@teste.com");
        req.setPassword("123456");
        Usuario user = new Usuario();
        user.setEmail("teste@teste.com");

        Mockito.when(userService.authenticate(req.getEmail(), req.getPassword())).thenReturn(user);
        Mockito.when(jwtUtil.generateToken(user.getEmail())).thenReturn("token-jwt");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso"))
                .andExpect(jsonPath("$.data.accessToken").value("token-jwt"));
    }

    @Test
    void verifyTokenTest() throws Exception {
        TokenValidationRequest req = new TokenValidationRequest();
        req.setEmail("teste@teste.com");
        req.setToken(1234);

        TokenModel tokenModel = new TokenModel();
        tokenModel.setEmail("teste@teste.com");
        tokenModel.setToken(1234);
        tokenModel.setDataCriacao(LocalDateTime.now().minusMinutes(1));

        Mockito.when(userService.verifyResetToken(req.getEmail(), req.getToken()))
                .thenReturn(tokenModel);

        mockMvc.perform(post("/auth/verify-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token válido"));
    }
}