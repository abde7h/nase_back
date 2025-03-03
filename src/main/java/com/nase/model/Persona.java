package com.nase.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "persona")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "numero_telefono", unique = true)
    private String numeroTelefono;

    @Column(name = "correo_electronico", unique = true)
    private String correoElectronico;

    @Column(name = "ip_publica")
    private String ipPublica;

    @Column(name = "ip_privada")
    private String ipPrivada;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento eventoRegistrado;

    @Column(name = "latitud_ultima")
    private Double latitudUltima;

    @Column(name = "longitud_ultima")
    private Double longitudUltima;

    @Column(name = "presente_evento")
    private Boolean presenteEvento = false;

    @Column(name = "vip")
    private Boolean vip = false;

    // Constructores, getters y setters
    public Persona() {}

    public Persona(String nombre, String apellido, String numeroTelefono, String correoElectronico) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroTelefono = numeroTelefono;
        this.correoElectronico = correoElectronico;
        this.ultimoAcceso = LocalDateTime.now();
        this.vip = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getIpPublica() {
        return ipPublica;
    }

    public void setIpPublica(String ipPublica) {
        this.ipPublica = ipPublica;
    }

    public String getIpPrivada() {
        return ipPrivada;
    }

    public void setIpPrivada(String ipPrivada) {
        this.ipPrivada = ipPrivada;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public Evento getEventoRegistrado() {
        return eventoRegistrado;
    }

    public void setEventoRegistrado(Evento eventoRegistrado) {
        this.eventoRegistrado = eventoRegistrado;
    }

    public Double getLatitudUltima() {
        return latitudUltima;
    }

    public void setLatitudUltima(Double latitudUltima) {
        this.latitudUltima = latitudUltima;
    }

    public Double getLongitudUltima() {
        return longitudUltima;
    }

    public void setLongitudUltima(Double longitudUltima) {
        this.longitudUltima = longitudUltima;
    }

    public Boolean getPresenteEvento() {
        return presenteEvento;
    }

    public void setPresenteEvento(Boolean presenteEvento) {
        this.presenteEvento = presenteEvento;
    }

    public Boolean getVip() {
        return vip;
    }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }
} 