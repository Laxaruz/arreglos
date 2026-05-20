package com.ezcerts.app.service;

import com.ezcerts.app.model.Empleado;
import java.util.List;
import java.util.Optional;

public interface EmpleadoService {
    Empleado crearEmpleado(Empleado empleado);

    Empleado actualizarEmpleado(Empleado empleado);

    Optional<Empleado> buscarPorId(Long id);

    Optional<Empleado> buscarPorUsuarioId(Long usuarioId);

    Optional<Empleado> buscarPorNumeroDocumento(String numeroDocumento);

    List<Empleado> listar();

    void eliminarEmpleado(Long id);
}
