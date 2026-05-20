package com.ezcerts.app.service.impl;

import com.ezcerts.app.model.EstadoSolicitud;
import com.ezcerts.app.model.SolicitudVacaciones;
import com.ezcerts.app.repository.SolicitudVacacionesRepository;
import com.ezcerts.app.service.SolicitudVacacionesService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudVacacionesServiceImpl implements SolicitudVacacionesService {
    private final SolicitudVacacionesRepository solicitudVacacionesRepository;

    public SolicitudVacacionesServiceImpl(SolicitudVacacionesRepository solicitudVacacionesRepository) {
        this.solicitudVacacionesRepository = solicitudVacacionesRepository;
    }

    @Override
    public SolicitudVacaciones crear(SolicitudVacaciones solicitud) {
        return solicitudVacacionesRepository.save(solicitud);
    }

    @Override
    public Optional<SolicitudVacaciones> buscarPorId(Long id) {
        return solicitudVacacionesRepository.findById(id);
    }

    @Override
    public List<SolicitudVacaciones> listarPorEmpleado(Long empleadoId) {
        return solicitudVacacionesRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    public List<SolicitudVacaciones> listarPendientes() {
        return solicitudVacacionesRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    @Override
    @Transactional
    public SolicitudVacaciones actualizarEstado(Long id, EstadoSolicitud estado, String comentarioRevision) {
        SolicitudVacaciones solicitud = solicitudVacacionesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (estado != EstadoSolicitud.PENDIENTE && (comentarioRevision == null || comentarioRevision.isBlank())) {
            throw new IllegalArgumentException("El comentario es obligatorio para aprobar o rechazar");
        }

        solicitud.setEstado(estado);
        solicitud.setComentarioRevision(comentarioRevision);
        return solicitudVacacionesRepository.save(solicitud);
    }
}

