package com.ezcerts.app.service.impl;

import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.model.Empleado;
import com.ezcerts.app.model.Rol;
import com.ezcerts.app.dto.UsuarioCrearDto;
import com.ezcerts.app.repository.UsuarioRepository;
import com.ezcerts.app.repository.EmpleadoRepository;
import com.ezcerts.app.repository.CertificadoRepository;
import com.ezcerts.app.repository.SolicitudVacacionesRepository;
import com.ezcerts.app.service.UsuarioService;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CertificadoRepository certificadoRepository;
    private final SolicitudVacacionesRepository solicitudVacacionesRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository, CertificadoRepository certificadoRepository, SolicitudVacacionesRepository solicitudVacacionesRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.certificadoRepository = certificadoRepository;
        this.solicitudVacacionesRepository = solicitudVacacionesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarUsuarioYEmpleado(UsuarioCrearDto dto) throws Exception {
        // 1. Crear e instanciar la entidad Usuario
        Usuario nuevoUsuario = new Usuario();
        String username = dto.getNombre().split(" ")[0]; // Generación básica del username
        nuevoUsuario.setUsername(username); 
        nuevoUsuario.setCorreo(dto.getCorreo());
        nuevoUsuario.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        nuevoUsuario.setRol(Rol.valueOf(dto.getRol().toUpperCase()));
        nuevoUsuario.setActivo(dto.isActivo());
        
        // Guardar el usuario primero
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 2. Siempre crear registro de empleado para conservar nombre/cédula en listados de usuarios
        Empleado nuevoEmpleado = new Empleado();
        nuevoEmpleado.setNombreCompleto(dto.getNombre());
        nuevoEmpleado.setNumeroDocumento(dto.getCedula());

        // Asignar parámetros con validaciones de nulos para evitar fallos por campos obligatorios
        nuevoEmpleado.setDepartamento(dto.getArea() != null && !dto.getArea().isEmpty() ? dto.getArea() : "General");
        nuevoEmpleado.setCargo(dto.getCargo() != null && !dto.getCargo().isEmpty() ? dto.getCargo() : "Analista");
        nuevoEmpleado.setFechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now());
        nuevoEmpleado.setSalario(dto.getSalario() != null ? dto.getSalario() : BigDecimal.ZERO);

        // REGLA CRÍTICA: Vincular el objeto usuario guardado (relación foreign key)
        nuevoEmpleado.setUsuario(usuarioGuardado);

        // Guardar empleado
        empleadoRepository.save(nuevoEmpleado);
    }

    @Override
    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public List<Usuario> buscarUsuarios(String parametro) {
        if (parametro == null || parametro.trim().isEmpty()) {
            return listar();
        }
        return usuarioRepository.findByFiltro(parametro.trim());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void actualizarUsuarioYEmpleado(Long id, UsuarioCrearDto dto) throws Exception {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new Exception("Usuario no encontrado"));
        
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(Rol.valueOf(dto.getRol().toUpperCase()));
        usuario.setActivo(dto.isActivo());
        
        if (dto.getContrasena() != null && !dto.getContrasena().trim().isEmpty()) {
            usuario.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        }
        
        usuarioRepository.save(usuario);

        Empleado empleado = usuario.getEmpleado();
        if (empleado == null) {
            empleado = new Empleado();
            empleado.setUsuario(usuario);
        }
        empleado.setNombreCompleto(dto.getNombre());
        empleado.setNumeroDocumento(dto.getCedula());
        empleado.setDepartamento(dto.getArea() != null && !dto.getArea().isEmpty() ? dto.getArea() : "General");
        empleado.setCargo(dto.getCargo() != null && !dto.getCargo().isEmpty() ? dto.getCargo() : "Analista");
        empleado.setFechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now());
        empleado.setSalario(dto.getSalario() != null ? dto.getSalario() : BigDecimal.ZERO);
        empleadoRepository.save(empleado);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuarioId(id);
        if (empleadoOpt.isPresent()) {
            Empleado emp = empleadoOpt.get();
            certificadoRepository.deleteByEmpleadoId(emp.getId());
            solicitudVacacionesRepository.deleteByEmpleadoId(emp.getId());
        }
        empleadoRepository.deleteByUsuarioId(id);
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void inactivarUsuario(Long id) {
        usuarioRepository.findById(id).ifPresent(usuario -> {
            usuario.setActivo(false);
            usuarioRepository.save(usuario);
        });
    }
}
