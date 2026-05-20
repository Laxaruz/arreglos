package com.ezcerts.app.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 30)
    private String numeroDocumento;

    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Column(nullable = false, length = 80)
    private String cargo;

    @Column(nullable = false, length = 80)
    private String departamento;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salario;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "empleado")
    private List<Certificado> certificados = new ArrayList<>();

    @OneToMany(mappedBy = "empleado")
    private List<SolicitudVacaciones> solicitudesVacaciones = new ArrayList<>();

    public Empleado() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Certificado> getCertificados() {
        return certificados;
    }

    public void setCertificados(List<Certificado> certificados) {
        this.certificados = certificados;
    }

    public List<SolicitudVacaciones> getSolicitudesVacaciones() {
        return solicitudesVacaciones;
    }

    public void setSolicitudesVacaciones(List<SolicitudVacaciones> solicitudesVacaciones) {
        this.solicitudesVacaciones = solicitudesVacaciones;
    }
}

