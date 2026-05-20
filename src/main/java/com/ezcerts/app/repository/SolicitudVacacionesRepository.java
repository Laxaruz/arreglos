package com.ezcerts.app.repository;

import com.ezcerts.app.model.EstadoSolicitud;
import com.ezcerts.app.model.SolicitudVacaciones;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudVacacionesRepository extends JpaRepository<SolicitudVacaciones, Long> {
    List<SolicitudVacaciones> findByEstado(EstadoSolicitud estado);

    List<SolicitudVacaciones> findByEmpleadoId(Long empleadoId);
    
    void deleteByEmpleadoId(Long empleadoId);
}
