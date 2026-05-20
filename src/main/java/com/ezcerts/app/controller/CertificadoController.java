package com.ezcerts.app.controller;

import com.ezcerts.app.model.Empleado;
import com.ezcerts.app.model.Usuario;
import com.ezcerts.app.service.EmpleadoService;
import com.ezcerts.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;

import com.ezcerts.app.model.Certificado;
import com.ezcerts.app.model.TipoCertificado;
import com.ezcerts.app.repository.CertificadoRepository;
import com.ezcerts.app.service.CertificadoService;

@Controller
@RequestMapping("/dashboard/empleado/solicitar-certificado")
public class CertificadoController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private CertificadoRepository certificadoRepository;

    private String resolverPlantillaPdf(TipoCertificado tipo) {
        // Centraliza el mapeo por si luego se separan plantillas por tipo.
        switch (tipo) {
            case LABORAL:
            case INGRESOS:
            case EXPERIENCIA:
            case ESTUDIOS:
                return "pdf-certificado";
            default:
                return "pdf-certificado";
        }
    }

    private Empleado obtenerEmpleadoActual(Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.buscarPorUsername(username).orElse(null);
        if (usuario != null) {
            Empleado empleado = empleadoService.buscarPorUsuarioId(usuario.getId()).orElse(null);
            if (empleado == null) {
                System.out.println("ADVERTENCIA: El usuario " + username + " no tiene un registro de Empleado asociado en la DB.");
                // Retornar un empleado mock temporal para evitar fallos si la bd quedó a medias
                Empleado mock = new Empleado();
                mock.setUsuario(usuario);
                mock.setNombreCompleto(username + " (Sin Perfil)");
                mock.setNumeroDocumento("N/A");
                mock.setCargo("N/A");
                mock.setDepartamento("N/A");
                mock.setFechaIngreso(LocalDate.now());
                mock.setSalario(java.math.BigDecimal.ZERO);
                return mock;
            }
            return empleado;
        }
        System.out.println("ADVERTENCIA: No se encontró el usuario " + username + " en la tabla usuarios.");
        return null;
    }

    // Step 2: Datos
    @GetMapping("/datos")
    public String pasoDatos(@RequestParam(name = "tipo", required = true) String tipo,
                            Authentication authentication, Model model) {
        Empleado empleado = obtenerEmpleadoActual(authentication);
        if (empleado == null) {
            return "redirect:/dashboard/empleado";
        }
        
        model.addAttribute("tipo", tipo);
        model.addAttribute("empleado", empleado);
        
        return "solicitar-certificado-datos";
    }

    // Step 3: Vista Previa
    @PostMapping("/vista-previa")
    public String pasoVistaPrevia(@RequestParam("tipo") String tipo,
                                  Authentication authentication, Model model) {
        Empleado empleado = obtenerEmpleadoActual(authentication);
        if (empleado == null) {
            return "redirect:/dashboard/empleado";
        }
        
        model.addAttribute("tipo", tipo);
        model.addAttribute("empleado", empleado);
        
        // Add dynamic date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        model.addAttribute("fechaActual", today.format(formatter));
        
        return "solicitar-certificado-previa";
    }

    // Step 4: Generar PDF y Redirigir a Exito
    @PostMapping("/generar")
    public String generarCertificadoPdf(@RequestParam("tipo") String tipo,
                                        Authentication authentication,
                                        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        try {
            Empleado empleado = obtenerEmpleadoActual(authentication);
            if (empleado == null) {
                return "redirect:/dashboard/empleado";
            }

            String tipoNormalizado = (tipo == null) ? "" : tipo.trim().toUpperCase();
            TipoCertificado tipoCertificado;
            try {
                tipoCertificado = TipoCertificado.valueOf(tipoNormalizado);
            } catch (IllegalArgumentException ex) {
                return "redirect:/dashboard/empleado/solicitar-certificado?error=true";
            }

            if (empleado.getSalario() == null) {
                empleado.setSalario(java.math.BigDecimal.ZERO);
            }

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            
            // Set Thymeleaf context
            Context context = new Context();
            context.setVariable("tipo", tipoCertificado.name());
            context.setVariable("empleado", empleado);
            context.setVariable("fechaActual", today.format(formatter));

            // Procesar plantilla HTML a String
            String plantillaPdf = resolverPlantillaPdf(tipoCertificado);
            String html = templateEngine.process(plantillaPdf, context);

            // Generar PDF con Flying Saucer
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);

            byte[] pdfBytes = outputStream.toByteArray();

            // Guardar archivo fsicamente
            String directory = "uploads/certificados/";
            Files.createDirectories(Paths.get(directory));
            String fileName = "Certificado_" + tipoCertificado.name() + "_" + empleado.getId() + "_" + System.currentTimeMillis() + ".pdf";
            String filePathString = directory + fileName;
            Files.write(Paths.get(filePathString), pdfBytes);

            // Registrar en base de datos
            Certificado certificado = new Certificado();
            certificado.setCodigoReferencia("CERT-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
            certificado.setFechaGeneracion(LocalDateTime.now());
            certificado.setRutaArchivoPDF(filePathString);
            certificado.setTipo(tipoCertificado);
            certificado.setEmpleado(empleado);
            Certificado savedCertificado = certificadoRepository.save(certificado);

            return "redirect:/dashboard/empleado/solicitar-certificado/exito?id=" + savedCertificado.getId();
        } catch (Exception e) {
            System.err.println("❌ ERROR SEVERO AL GENERAR CERTIFICADO:");
            e.printStackTrace();
            return "redirect:/dashboard/empleado/solicitar-certificado?error=true";
        }
    }

    @GetMapping("/exito")
    public String pasoExito(@RequestParam("id") Long id, Authentication authentication, Model model) {
        Empleado empleado = obtenerEmpleadoActual(authentication);
        if (empleado == null) return "redirect:/dashboard/empleado";

        Certificado cert = certificadoRepository.findById(id).orElse(null);
        if (cert == null || !cert.getEmpleado().getId().equals(empleado.getId())) {
            return "redirect:/dashboard/empleado/solicitar-certificado";
        }

        model.addAttribute("certificado", cert);
        return "solicitar-certificado-exito";
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<byte[]> descargarCertificado(@PathVariable Long id, Authentication authentication) {
        try {
            Empleado empleado = obtenerEmpleadoActual(authentication);
            if (empleado == null) return ResponseEntity.status(403).build();

            Certificado cert = certificadoRepository.findById(id).orElse(null);
            if (cert == null || !cert.getEmpleado().getId().equals(empleado.getId())) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(cert.getRutaArchivoPDF());
            byte[] pdfBytes = Files.readAllBytes(path);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Certificado_" + cert.getTipo() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
