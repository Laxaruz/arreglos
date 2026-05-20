package com.ezcerts.app.service;

import com.ezcerts.app.model.Certificado;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CertificadoService {
    Certificado guardar(Certificado certificado);

    Optional<Certificado> buscarPorId(Long id);

    List<Certificado> listarPorEmpleado(Long empleadoId);

    List<Certificado> listarPorEmpleadoEntreFechas(
            Long empleadoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}

