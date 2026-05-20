package com.ezcerts.app.repository;

import com.ezcerts.app.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByCorreo(String correo);

    @Query("SELECT u FROM Usuario u LEFT JOIN u.empleado e WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(e.nombreCompleto) LIKE LOWER(CONCAT('%', :filtro, '%')) OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    java.util.List<Usuario> findByFiltro(@Param("filtro") String filtro);

    boolean existsByUsername(String username);

    boolean existsByCorreo(String correo);
}
