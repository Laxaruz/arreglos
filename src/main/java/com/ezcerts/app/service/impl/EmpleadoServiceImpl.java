package com.ezcerts.app.service.impl;

import com.ezcerts.app.model.Empleado;
import com.ezcerts.app.repository.EmpleadoRepository;
import com.ezcerts.app.service.EmpleadoService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public Empleado crearEmpleado(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    @Override
    public Empleado actualizarEmpleado(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    @Override
    public Optional<Empleado> buscarPorId(Long id) {
        return empleadoRepository.findById(id);
    }

    @Override
    public Optional<Empleado> buscarPorUsuarioId(Long usuarioId) {
        return empleadoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Empleado> buscarPorNumeroDocumento(String numeroDocumento) {
        return empleadoRepository.findByNumeroDocumento(numeroDocumento);
    }

    @Override
    public List<Empleado> listar() {
        return empleadoRepository.findAll();
    }

    @Override
    public void eliminarEmpleado(Long id) {
        empleadoRepository.deleteById(id);
    }
}
