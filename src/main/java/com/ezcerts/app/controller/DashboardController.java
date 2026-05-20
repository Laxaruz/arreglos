package com.ezcerts.app.controller;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import com.ezcerts.app.service.UsuarioService;
import com.ezcerts.app.service.EmpleadoService;
import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.model.Empleado;
import com.ezcerts.app.model.Rol;
import com.ezcerts.app.dto.UsuarioCrearDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import com.ezcerts.app.repository.CertificadoRepository;
import com.ezcerts.app.model.Certificado;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import com.ezcerts.app.service.SolicitudVacacionesService;
import com.ezcerts.app.model.SolicitudVacaciones;
import com.ezcerts.app.model.EstadoSolicitud;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.YearMonth;
import java.time.LocalDateTime;
import com.ezcerts.app.repository.SolicitudVacacionesRepository;
import java.time.format.TextStyle;
import java.util.Locale;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private SolicitudVacacionesService solicitudVacacionesService;

    @Autowired
    private SolicitudVacacionesRepository solicitudVacacionesRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (roles.contains("ROLE_EMPLEADO")) {
            return "redirect:/dashboard/empleado";
        }

        if (roles.contains("ROLE_RRHH") || roles.contains("ROLE_ADMIN")) {
            return "redirect:/dashboard/admin";
        }

        return "redirect:/login";
    }

    @GetMapping("/dashboard/empleado")
    public String dashboardEmpleado(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username).orElse(null);
        List<Certificado> actividades = Collections.emptyList();
        List<SolicitudVacaciones> solicitudesVacaciones = Collections.emptyList();

        if (usuario != null) {
            Empleado empleado = empleadoService.buscarPorUsuarioId(usuario.getId()).orElse(null);
            if (empleado != null) {
                actividades = certificadoRepository.findByEmpleadoIdOrderByFechaGeneracionDesc(empleado.getId());
                solicitudesVacaciones = new ArrayList<>(solicitudVacacionesService.listarPorEmpleado(empleado.getId()));
                solicitudesVacaciones.sort(Comparator.comparing(SolicitudVacaciones::getFechaInicio).reversed());
            }
        }

        model.addAttribute("actividades", actividades);
        model.addAttribute("cantidadCertificados", actividades.size());
        model.addAttribute("cantidadVacaciones", solicitudesVacaciones.size());
        model.addAttribute("ultimaSolicitudVacaciones", solicitudesVacaciones.isEmpty() ? null : solicitudesVacaciones.get(0));
        return "dashboard-empleado";
    }

    @GetMapping("/dashboard/empleado/solicitar-certificado")
    public String solicitarCertificado() {
        return "solicitar-certificado";
    }

    @GetMapping("/dashboard/empleado/mis-vacaciones")
    public String misVacaciones(Authentication authentication, Model model) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        Empleado empleado = empleadoService.buscarPorUsuarioId(usuario.getId()).orElse(null);
        if (empleado == null) {
            return "redirect:/dashboard/empleado";
        }

        List<SolicitudVacaciones> solicitudes = new ArrayList<>(solicitudVacacionesService.listarPorEmpleado(empleado.getId()));
        solicitudes.sort(Comparator.comparing(SolicitudVacaciones::getFechaInicio).reversed());

        long pendientes = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE).count();
        long aprobadas = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitud.APROBADA).count();
        long rechazadas = solicitudes.stream().filter(s -> s.getEstado() == EstadoSolicitud.RECHAZADA).count();

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("aprobadas", aprobadas);
        model.addAttribute("rechazadas", rechazadas);
        return "mis-vacaciones";
    }

    @PostMapping("/dashboard/empleado/mis-vacaciones/solicitar")
    public String solicitarVacaciones(Authentication authentication,
                                      @RequestParam("fechaInicio") LocalDate fechaInicio,
                                      @RequestParam("fechaFin") LocalDate fechaFin,
                                      org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (fechaInicio.isAfter(fechaFin)) {
            redirectAttributes.addFlashAttribute("error", "La fecha de inicio no puede ser mayor que la fecha final.");
            return "redirect:/dashboard/empleado/mis-vacaciones";
        }

        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        Empleado empleado = empleadoService.buscarPorUsuarioId(usuario.getId()).orElse(null);
        if (empleado == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró información del empleado.");
            return "redirect:/dashboard/empleado/mis-vacaciones";
        }

        SolicitudVacaciones solicitud = new SolicitudVacaciones();
        solicitud.setFechaInicio(fechaInicio);
        solicitud.setFechaFin(fechaFin);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setEmpleado(empleado);
        solicitudVacacionesService.crear(solicitud);

        redirectAttributes.addFlashAttribute("exito", "Tu solicitud de vacaciones fue enviada correctamente.");
        return "redirect:/dashboard/empleado/mis-vacaciones";
    }

    @GetMapping("/dashboard/admin")
    public String dashboardAdmin() {
        return "dashboard-admin";
    }

    @GetMapping("/dashboard/admin/solicitudes-pendientes")
    public String solicitudesPendientes(Authentication authentication, Model model) {
        if (!esRrhhOAdmin(authentication)) {
            return "redirect:/dashboard";
        }

        List<SolicitudVacaciones> solicitudes = new ArrayList<>(solicitudVacacionesService.listarPendientes());
        solicitudes.sort(Comparator.comparing(SolicitudVacaciones::getFechaInicio));
        model.addAttribute("solicitudesPendientes", solicitudes);
        return "solicitudes-pendientes";
    }

    @GetMapping("/dashboard/admin/reportes-mes")
    public String reportesDelMes(Authentication authentication, Model model) {
        if (!esRrhhOAdmin(authentication)) {
            return "redirect:/dashboard";
        }

        YearMonth mesActual = YearMonth.now();
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();
        LocalDateTime inicioMesDateTime = inicioMes.atStartOfDay();
        LocalDateTime finMesDateTime = finMes.atTime(23, 59, 59);

        List<Certificado> certificadosMes = certificadoRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fechaGeneracion"))
                .stream()
                .filter(c -> !c.getFechaGeneracion().isBefore(inicioMesDateTime) && !c.getFechaGeneracion().isAfter(finMesDateTime))
                .collect(Collectors.toList());

        List<SolicitudVacaciones> solicitudesMes = solicitudVacacionesRepository.findAll()
                .stream()
                .filter(s -> !s.getFechaFin().isBefore(inicioMes) && !s.getFechaInicio().isAfter(finMes))
                .sorted(Comparator.comparing(SolicitudVacaciones::getFechaInicio).reversed())
                .collect(Collectors.toList());

        long totalSolicitudes = solicitudesMes.size();
        long pendientes = solicitudesMes.stream().filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE).count();
        long aprobadas = solicitudesMes.stream().filter(s -> s.getEstado() == EstadoSolicitud.APROBADA).count();
        long rechazadas = solicitudesMes.stream().filter(s -> s.getEstado() == EstadoSolicitud.RECHAZADA).count();

        String mesTexto = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "CO"));
        if (!mesTexto.isEmpty()) {
            mesTexto = mesTexto.substring(0, 1).toUpperCase() + mesTexto.substring(1);
        }

        model.addAttribute("mesTexto", mesTexto);
        model.addAttribute("anioActual", mesActual.getYear());
        model.addAttribute("totalCertificadosMes", certificadosMes.size());
        model.addAttribute("totalSolicitudesMes", totalSolicitudes);
        model.addAttribute("pendientesMes", pendientes);
        model.addAttribute("aprobadasMes", aprobadas);
        model.addAttribute("rechazadasMes", rechazadas);
        model.addAttribute("certificadosMes", certificadosMes);
        model.addAttribute("solicitudesMes", solicitudesMes);
        return "reportes-mes";
    }

    @PostMapping("/dashboard/admin/solicitudes-pendientes/actualizar-estado")
    public String actualizarEstadoSolicitud(Authentication authentication,
                                           @RequestParam("id") Long id,
                                           @RequestParam("estado") String estado,
                                           @RequestParam("comentarioRevision") String comentarioRevision,
                                           RedirectAttributes redirectAttributes) {
        if (!esRrhhOAdmin(authentication)) {
            return "redirect:/dashboard";
        }

        try {
            EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(estado.toUpperCase());
            solicitudVacacionesService.actualizarEstado(id, nuevoEstado, comentarioRevision);
            redirectAttributes.addFlashAttribute("exito", "Solicitud actualizada correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la solicitud.");
        }
        return "redirect:/dashboard/admin/solicitudes-pendientes";
    }

    @GetMapping("/dashboard/admin/certificados-generados")
    public String certificadosGenerados(Model model) {
        List<Certificado> certificados = certificadoRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fechaGeneracion"));
        model.addAttribute("certificados", certificados);
        
        long total = certificados.size();
        long esteMes = certificados.stream().filter(c -> c.getFechaGeneracion().getMonth() == LocalDate.now().getMonth() && c.getFechaGeneracion().getYear() == LocalDate.now().getYear()).count();
        long hoy = certificados.stream().filter(c -> c.getFechaGeneracion().toLocalDate().isEqual(LocalDate.now())).count();
        int anio = LocalDate.now().getYear();

        model.addAttribute("total", total);
        model.addAttribute("esteMes", esteMes);
        model.addAttribute("hoy", hoy);
        model.addAttribute("anio", anio);

        return "certificados-generados";
    }

    @GetMapping("/dashboard/usuarios-activos")
    public String usuariosActivos(@RequestParam(required = false) String buscar, Model model) {
        model.addAttribute("usuarios", usuarioService.buscarUsuarios(buscar));
        model.addAttribute("buscar", buscar);
        return "usuarios-activos";
    }

    @GetMapping("/dashboard/usuarios-activos/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuarioDto", new UsuarioCrearDto());
        return "crear-usuario";
    }

    @PostMapping("/dashboard/usuarios-activos/crear")
    public String procesarFormularioCrear(@ModelAttribute("usuarioDto") UsuarioCrearDto dto, Model model) {
        if (!dto.getContrasena().equals(dto.getConfirmarContrasena())) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "crear-usuario";
        }
        
        try {
            // Delega TODA la lógica de inserción transaccional al Service
            usuarioService.registrarUsuarioYEmpleado(dto);
            
            return "redirect:/dashboard/usuarios-activos?exito";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            System.err.println("Database error: " + ex.getMessage());
            model.addAttribute("error", "Error crítico: El correo o la cédula que intenta registrar ya existen. Por favor verifique los datos.");
            return "crear-usuario";
        } catch (Exception e) {
            e.printStackTrace(); // Imprime la traza en la terminal para identificar mapeos fallidos
            model.addAttribute("error", "Error interno al crear el usuario. Por favor revise su consola/log: " + e.getMessage());
            return "crear-usuario";
        }
    }

    @GetMapping("/dashboard/usuarios-activos/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        if (usuario == null) {
            return "redirect:/dashboard/usuarios-activos";
        }
        
        UsuarioCrearDto dto = new UsuarioCrearDto();
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol().name());
        dto.setActivo(usuario.isActivo());
        
        if (usuario.getEmpleado() != null) {
            Empleado e = usuario.getEmpleado();
            dto.setNombre(e.getNombreCompleto());
            dto.setCedula(e.getNumeroDocumento());
            dto.setCargo(e.getCargo());
            dto.setArea(e.getDepartamento());
            dto.setFechaIngreso(e.getFechaIngreso());
            dto.setSalario(e.getSalario());
        } else {
            dto.setNombre(usuario.getUsername());
        }
        
        model.addAttribute("usuarioDto", dto);
        model.addAttribute("usuarioId", usuario.getId());
        return "editar-usuario";
    }

    @PostMapping("/dashboard/usuarios-activos/actualizar")
    public String procesarFormularioEditar(@RequestParam("id") Long id, @ModelAttribute("usuarioDto") UsuarioCrearDto dto, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes, Model model) {
        if (dto.getContrasena() != null && !dto.getContrasena().trim().isEmpty()) {
            if (!dto.getContrasena().equals(dto.getConfirmarContrasena())) {
                model.addAttribute("error", "Las contraseñas no coinciden");
                model.addAttribute("usuarioId", id);
                return "editar-usuario";
            }
        }
        
        try {
            usuarioService.actualizarUsuarioYEmpleado(id, dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario actualizado correctamente");
            return "redirect:/dashboard/usuarios-activos";
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            model.addAttribute("error", "Error: El correo o la cédula que intenta registrar ya existen en otro usuario.");
            model.addAttribute("usuarioId", id);
            return "editar-usuario";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error interno al actualizar el usuario: " + e.getMessage());
            model.addAttribute("usuarioId", id);
            return "editar-usuario";
        }
    }

    @PostMapping("/dashboard/usuarios-activos/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario eliminado correctamente");
            return "redirect:/dashboard/usuarios-activos";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Hubo un problema al eliminar el usuario");
            return "redirect:/dashboard/usuarios-activos";
        }
    }

    private boolean esRrhhOAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return roles.contains("ROLE_RRHH") || roles.contains("ROLE_ADMIN");
    }
}
