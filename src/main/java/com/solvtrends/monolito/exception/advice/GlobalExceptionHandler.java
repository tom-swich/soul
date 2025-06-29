package com.solvtrends.monolito.exception.advice;

import com.solvtrends.monolito.exception.EmailSendException;
import com.solvtrends.monolito.exception.FieldErrorDetail;
import com.solvtrends.monolito.exception.ProblemDetails;
import com.solvtrends.monolito.exception.TokenPersistException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ProblemDetails> handleUserNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "https://solvtrends.com/errors//user-not-found",
                "Usuário não encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                generateTraceId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ProblemDetails> handleMessaging(MessagingException ex, HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "https://solvtrends.com/errors/email-failure",
                "Erro ao enviar e-mail",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI(),
                generateTraceId()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleGeneric(Exception ex, HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "https://solvtrends.com/errors/internal",
                "Erro interno do servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI(),
                generateTraceId()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ProblemDetails> handleEmailSend(EmailSendException ex, HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "https://solvtrends.com/errors/email-failure",
                "Erro ao enviar e-mail",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI(),
                generateTraceId()
        );
        return ResponseEntity.status(500)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(TokenPersistException.class)
    public ResponseEntity<ProblemDetails> handleTokenPersist(TokenPersistException ex, HttpServletRequest request) {
        ProblemDetails problem = new ProblemDetails(
                "https://solvtrends.com/errors/token-save-failure",
                "Erro ao salvar token",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI(),
                generateTraceId()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetails> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldErrorDetail> errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ProblemDetails problem = new ProblemDetails(
                "https://weup.com.br/errors/validation",
                "Erro de validação de campos",
                HttpStatus.BAD_REQUEST.value(),
                "Um ou mais campos estão inválidos",
                request.getRequestURI(),
                generateTraceId()
        );
        problem.setErrors(errorDetails);

        return ResponseEntity.status(400)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}

