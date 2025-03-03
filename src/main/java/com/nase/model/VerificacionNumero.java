package com.nase.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verificaciones")
public class VerificacionNumero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numero;

    @Column(name = "existe_usuario")
    private boolean existeUsuario;

    @Column(name = "esta_activo")
    private boolean estaActivo;

    @Column(name = "tiene_premium")
    private boolean tienePremium;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    @Column(name = "tipo_verificacion", length = 50)
    private String tipoVerificacion;

    // Constructor vacío requerido por JPA
    public VerificacionNumero() {
    }

    // Constructor con parámetros
    public VerificacionNumero(String numero, boolean existeUsuario, boolean estaActivo,
                              boolean tienePremium, String mensaje, String tipoVerificacion) {
        this.numero = numero;
        this.existeUsuario = existeUsuario;
        this.estaActivo = estaActivo;
        this.tienePremium = tienePremium;
        this.mensaje = mensaje;
        this.tipoVerificacion = tipoVerificacion;
        this.fechaVerificacion = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public boolean isExisteUsuario() {
        return existeUsuario;
    }

    public void setExisteUsuario(boolean existeUsuario) {
        this.existeUsuario = existeUsuario;
    }

    public boolean isEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public boolean isTienePremium() {
        return tienePremium;
    }

    public void setTienePremium(boolean tienePremium) {
        this.tienePremium = tienePremium;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaVerificacion() {
        return fechaVerificacion;
    }

    public void setFechaVerificacion(LocalDateTime fechaVerificacion) {
        this.fechaVerificacion = fechaVerificacion;
    }

    public String getTipoVerificacion() {
        return tipoVerificacion;
    }

    public void setTipoVerificacion(String tipoVerificacion) {
        this.tipoVerificacion = tipoVerificacion;
    }

    @PrePersist
    protected void onCreate() {
        fechaVerificacion = LocalDateTime.now();
    }
}