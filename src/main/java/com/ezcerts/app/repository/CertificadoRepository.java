package com.ezcerts.app.repository;

import com.ezcerts.app.model.Certificado;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    List<Certificado> findByEmpleadoId(Long empleadoId);
    
    void deleteByEmpleadoId(Long empleadoId);
    
    List<Certificado> findByEmpleadoIdOrderByFechaGeneracionDesc(Long empleadoId);

    List<Certificado> findByEmpleadoIdAndFechaGeneracionBetween(
            Long empleadoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}
