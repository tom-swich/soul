package com.solvtrends.monolito.service;

import com.solvtrends.monolito.model.Usuario;
import com.solvtrends.monolito.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class UserDetailsSecurityService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));

        if (user.getContaAtiva() == null || !user.getContaAtiva()) {
            throw new DisabledException("Conta inativa. Entre em contato com o suporte.");
        }


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getContaAtiva(),
                true,
                true,
                true,
                new ArrayList<>()
        );
    }

    public Usuario getMeByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com email: " + email));
    }
}