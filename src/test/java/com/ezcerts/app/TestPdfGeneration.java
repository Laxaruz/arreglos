package com.ezcerts.app;
import com.ezcerts.app.model.Empleado;
import com.ezcerts.app.model.TipoCertificado;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
public class TestPdfGeneration {
    public static void main(String[] args) {
        try {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setPrefix("templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML");
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);
            Empleado empleado = new Empleado();
            empleado.setNombreCompleto("Prueba");
            empleado.setNumeroDocumento("12345");
            empleado.setCargo("Cargo");
            empleado.setDepartamento("Depto");
            empleado.setFechaIngreso(LocalDate.now());
            empleado.setSalario(new BigDecimal("1000"));
            String[] tipos = {"INGRESOS", "EXPERIENCIA", "ESTUDIOS"};
            for(String t : tipos) {
                Context context = new Context();
                context.setVariable("tipo", t);
                context.setVariable("empleado", empleado);
                context.setVariable("fechaActual", "hoy");
                String html = templateEngine.process("pdf-certificado", context);
                // System.out.println(html);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(outputStream);
                System.out.println("EXITO: " + t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}