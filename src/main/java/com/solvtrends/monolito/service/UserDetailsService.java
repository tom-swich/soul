package com.solvtrends.monolito.service;

import com.solvtrends.monolito.model.Usuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {

    UserDetails loadUserByEmail(String email) throws UsernameNotFoundException;

    Usuario getMeByEmail(String email) throws UsernameNotFoundException;

}
