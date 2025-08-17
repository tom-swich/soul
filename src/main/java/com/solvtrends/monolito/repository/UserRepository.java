package com.solvtrends.monolito.repository;

import com.solvtrends.monolito.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpf(String cpf);
    Optional<Usuario> findById(int id);
    Optional<Usuario> findByPassword(String password);
    Usuario findMeByEmail(String email);

}
