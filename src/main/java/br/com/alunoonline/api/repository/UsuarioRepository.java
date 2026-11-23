package br.com.alunoonline.api.repository;

import br.com.alunoonline.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByLoginIgnoreCase(String login);

    Optional<Usuario> findByLoginIgnoreCase(String login);
}
