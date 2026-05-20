package com.ezcerts.app.service;

import com.ezcerts.app.model.EstadoSolicitud;
import com.ezcerts.app.model.SolicitudVacaciones;
import java.util.List;
import java.util.Optional;

public interface SolicitudVacacionesService {
    SolicitudVacaciones crear(SolicitudVacaciones solicitud);

    Optional<SolicitudVacaciones> buscarPorId(Long id);

    List<SolicitudVacaciones> listarPorEmpleado(Long empleadoId);

    List<SolicitudVacaciones> listarPendientes();

    SolicitudVacaciones actualizarEstado(Long id, EstadoSolicitud estado, String comentarioRevision);
}

