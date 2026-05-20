package com.ezcerts.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificados")
public class Certificado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_referencia", nullable = false, unique = true, length = 40)
    private String codigoReferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoCertificado tipo;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "ruta_archivo_pdf", nullable = false, length = 500)
    private String rutaArchivoPDF;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    public Certificado() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoReferencia() {
        return codigoReferencia;
    }

    public void setCodigoReferencia(String codigoReferencia) {
        this.codigoReferencia = codigoReferencia;
    }

    public TipoCertificado getTipo() {
        return tipo;
    }

    public void setTipo(TipoCertificado tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getRutaArchivoPDF() {
        return rutaArchivoPDF;
    }

    public void setRutaArchivoPDF(String rutaArchivoPDF) {
        this.rutaArchivoPDF = rutaArchivoPDF;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
}

