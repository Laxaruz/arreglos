package com.ezcerts.app.repository;

import com.ezcerts.app.model.Empleado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByNumeroDocumento(String numeroDocumento);
    
    Optional<Empleado> findByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);

    boolean existsByNumeroDocumento(String numeroDocumento);
}
