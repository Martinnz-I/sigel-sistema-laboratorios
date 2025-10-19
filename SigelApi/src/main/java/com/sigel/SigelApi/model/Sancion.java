package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sanciones", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sancion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "aplicada_por")
    private Usuario aplicadaPor;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "sigel.severity_level", nullable = false)
    private SeverityLevel severidad;

    @Column(name = "dias_suspension", nullable = false)
    @Builder.Default
    private Integer diasSuspension = 0;

    @ManyToOne
    @JoinColumn(name = "prestamo_id")
    private Prestamo prestamo;
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "fecha_aplicacion")
    private LocalDateTime fechaAplicacion;

    @Column(name = "fecha_levantamiento")
    private LocalDateTime fechaLevantamiento;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean apelada = false;

    @Column(name = "fecha_apelacion")
    private LocalDateTime fechaApelacion;

    @Column(name = "motivo_apelacion", columnDefinition = "TEXT")
    private String motivoApelacion;

    @Column(name = "apelacion_aceptada")
    private Boolean apelacionAceptada;

    @Column(name = "created_at", nullable = false, unique = true)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        fechaAplicacion = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }
}