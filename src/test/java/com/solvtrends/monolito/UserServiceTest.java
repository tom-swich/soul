package com.solvtrends.monolito.service;

import com.solvtrends.monolito.exception.TokenExpiredException;
import com.solvtrends.monolito.model.TokenModel;
import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.repository.TokenRepository;
import com.solvtrends.monolito.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private EmailService emailService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@teste.com");
        usuario.setPassword(new BCryptPasswordEncoder().encode("123456"));
        usuario.setNome("Nome");
        usuario.setSobrenome("Sobrenome");
        usuario.setCpf("12345678909");
        usuario.setPaisTelefone("55");
        usuario.setTelefone(99999999L);
        usuario.setContaAtiva(true);
    }

    @Test
    void authenticate_DeveRetornarUsuario_QuandoCredenciaisCorretas() {
        when(userRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        Usuario result = userService.authenticate(usuario.getEmail(), "123456");
        assertEquals(usuario.getEmail(), result.getEmail());
    }

    @Test
    void authenticate_DeveLancarExcecao_QuandoSenhaIncorreta() {
        when(userRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        assertThrows(UsernameNotFoundException.class, () -> userService.authenticate(usuario.getEmail(), "errada"));
    }

    @Test
    void register_DeveSalvarUsuario() {
        when(userRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByCpf(usuario.getCpf())).thenReturn(Optional.empty());
        Usuario novo = new Usuario();
        novo.setEmail(usuario.getEmail());
        novo.setPassword("123456");
        novo.setNome(usuario.getNome());
        novo.setSobrenome(usuario.getSobrenome());
        novo.setCpf(usuario.getCpf());
        novo.setPaisTelefone(usuario.getPaisTelefone());
        novo.setTelefone(usuario.getTelefone());
        Usuario salvo = userService.register(novo);
        assertNotNull(salvo.getPassword());
    }

    @Test
    void register_DeveLancarExcecao_QuandoEmailInvalido() {
        Usuario novo = new Usuario();
        novo.setEmail("invalido");
        novo.setPassword("123456");
        novo.setNome("Nome");
        novo.setSobrenome("Sobrenome");
        novo.setCpf("12345678909");
        novo.setPaisTelefone("55");
        novo.setTelefone(99999999L);
        assertThrows(ValidationException.class, () -> userService.register(novo));
    }

    @Test
    void verifyResetToken_DeveLancarExcecao_QuandoTokenExpirado() {
        TokenModel tokenModel = new TokenModel();
        tokenModel.setToken(1234);
        tokenModel.setEmail(usuario.getEmail());
        tokenModel.setDataCriacao(LocalDateTime.now().minusMinutes(10));
        when(tokenRepository.findByEmailAndToken(usuario.getEmail(), 1234)).thenReturn(Optional.of(tokenModel));
        assertThrows(TokenExpiredException.class, () -> userService.verifyResetToken(usuario.getEmail(), 1234));
    }
}