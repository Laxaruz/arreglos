package com.ezcerts.app.service.impl;

import com.ezcerts.app.model.Certificado;
import com.ezcerts.app.repository.CertificadoRepository;
import com.ezcerts.app.service.CertificadoService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CertificadoServiceImpl implements CertificadoService {
    private final CertificadoRepository certificadoRepository;

    public CertificadoServiceImpl(CertificadoRepository certificadoRepository) {
        this.certificadoRepository = certificadoRepository;
    }

    @Override
    public Certificado guardar(Certificado certificado) {
        return certificadoRepository.save(certificado);
    }

    @Override
    public Optional<Certificado> buscarPorId(Long id) {
        return certificadoRepository.findById(id);
    }

    @Override
    public List<Certificado> listarPorEmpleado(Long empleadoId) {
        return certificadoRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    public List<Certificado> listarPorEmpleadoEntreFechas(
            Long empleadoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {
        return certificadoRepository.findByEmpleadoIdAndFechaGeneracionBetween(
                empleadoId,
                fechaInicio,
                fechaFin
        );
    }
}

