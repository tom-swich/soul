package com.solvtrends.monolito.service;

import com.solvtrends.monolito.exception.EmailSendException;
import com.solvtrends.monolito.exception.TokenExpiredException;
import com.solvtrends.monolito.exception.TokenPersistException;
import com.solvtrends.monolito.model.TokenModel;
import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.repository.TokenRepository;
import com.solvtrends.monolito.repository.UserRepository;
import com.solvtrends.monolito.util.CpfUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.validation.ValidationException;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    EmailService emailService;

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern CPF_REGEX = Pattern.compile("^\\d{11}$");

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElseThrow(() -> new UsernameNotFoundException("Credencias inválidas"));
    }

    public Usuario register(Usuario user) {
        validarUsuario(user);
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public TokenModel resetPassword(String email)  {
        Usuario usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para email: " + email));

        Random random = new Random();

        int token = ThreadLocalRandom.current().nextInt(1000, 10000);
        TokenModel tokenModel = new TokenModel();
        tokenModel.setToken(token);
        tokenModel.setEmail(usuario.getEmail());
        tokenModel.setUserId(usuario.getId());

        try {
            tokenRepository.save(tokenModel);
        } catch (RuntimeException e) {
            throw new TokenPersistException("Erro ao salvar token no banco de dados => ",e);
        }

        try {
            emailService.sendEmailResetPassword(usuario.getNome(), usuario.getEmail(), token);
        } catch (MessagingException e) {
            throw new EmailSendException("Erro ao enviar e-mail de redefinição de senha", e);
        }
        return tokenModel;
    }

    public TokenModel verifyResetToken(String email, int token) {
        TokenModel tokenModel = tokenRepository.findByEmailAndToken(email, token)
                .orElseThrow(TokenExpiredException::new);

        Instant agora = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();
        Instant criadoEm = tokenModel.getDataCriacao().atZone(zoneId).toInstant();

        Duration expiracao = Duration.ofMinutes(5);

        if (!(criadoEm != null && criadoEm.plus(expiracao).isAfter(agora))) {
            tokenRepository.delete(tokenModel);
            throw new TokenExpiredException();
        }

        return tokenModel;
    }

    public void resetUserPassword(String email, int token, String newPassword) {
        TokenModel tokenModel = verifyResetToken(email,token);

        if(tokenModel.getId() != null) {
            Usuario usuario = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            usuario.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(usuario);

            tokenRepository.delete(tokenModel);
        }
    }


    private void validarUsuario(Usuario u) {
        if (u.getEmail() == null || !EMAIL_REGEX.matcher(u.getEmail()).matches()) {
            throw new ValidationException("Email inválido.");
        }

        if (u.getPassword() == null || u.getPassword().length() < 6) {
            throw new ValidationException("Senha deve ter ao menos 6 caracteres.");
        }

        if (u.getNome() == null || u.getNome().isBlank()) {
            throw new ValidationException("Nome é obrigatório.");
        }

        if (u.getSobrenome() == null || u.getSobrenome().isBlank()) {
            throw new ValidationException("Sobrenome é obrigatório.");
        }

        if (u.getCpf() == null || !CPF_REGEX.matcher(u.getCpf()).matches() || !CpfUtil.isValid(u.getCpf())) {
            throw new ValidationException("CPF inválido.");
        }

        if (u.getPaisTelefone() == null || u.getPaisTelefone().isBlank()) {
            throw new ValidationException("País do telefone é obrigatório.");
        }

        if (String.valueOf(u.getTelefone()).length() < 8) {
            throw new ValidationException("Telefone inválido.");
        }

        if (userRepository.findByEmail(u.getEmail()).isPresent()) {
            throw new ValidationException("Já existe um usuário com este e-mail.");
        }

        if (userRepository.findByCpf(u.getCpf()).isPresent()) {
            throw new ValidationException("Já existe um usuário com este CPF.");
        }
    }
}
