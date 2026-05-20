package com.ezcerts.app.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaFixer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaFixer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureCertificadosTipoIsVarchar() {
        try {
            // Evita que la columna tipo quede restringida a un ENUM antiguo (ej. solo LABORAL).
            jdbcTemplate.execute("ALTER TABLE certificados MODIFY COLUMN tipo VARCHAR(30) NOT NULL");
        } catch (Exception ex) {
            // Si la estructura ya es correcta o la BD no requiere cambio, se ignora.
            System.out.println("INFO: No fue necesario ajustar columna certificados.tipo: " + ex.getMessage());
        }
    }
}
