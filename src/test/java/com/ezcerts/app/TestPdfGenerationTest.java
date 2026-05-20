package com.ezcerts.app;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.ezcerts.app.model.Empleado;
@SpringBootTest
public class TestPdfGenerationTest {
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Test
    public void testPdfs() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setNombreCompleto("Prueba");
        empleado.setNumeroDocumento("12345");
        empleado.setCargo("Cargo");
        empleado.setDepartamento("Depto");
        empleado.setFechaIngreso(LocalDate.now());
        empleado.setSalario(new BigDecimal("1000"));
        String[] tipos = {"INGRESOS", "EXPERIENCIA", "ESTUDIOS"};
        for(String t : tipos) {
            System.out.println("TESTING: " + t);
            Context context = new Context();
            context.setVariable("tipo", t);
            context.setVariable("empleado", empleado);
            context.setVariable("fechaActual", "hoy");
            String html = templateEngine.process("pdf-certificado", context);
            System.out.println("HTML GENERATED!");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            System.out.println("EXITO: " + t);
        }
    }
}