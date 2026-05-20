package com.ezcerts.app.dto;
public class UsuarioCrearDto {
    private String nombre;
    private String cedula;
    private String correo;
    private String contrasena;
    private String confirmarContrasena;
    private String rol;
    private String area;
    private String cargo;
    private java.math.BigDecimal salario;
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate fechaIngreso;
    private boolean activo = true;
    // Getters and Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getConfirmarContrasena() { return confirmarContrasena; }
    public void setConfirmarContrasena(String confirmarContrasena) { this.confirmarContrasena = confirmarContrasena; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public java.math.BigDecimal getSalario() { return salario; }
    public void setSalario(java.math.BigDecimal salario) { this.salario = salario; }
    public java.time.LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(java.time.LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
