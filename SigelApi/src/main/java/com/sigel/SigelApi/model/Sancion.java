package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.SeverityLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sanciones")
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
    @Column(nullable = false)
    private SeverityLevel severidad;

    private int diasSuspension;

    @ManyToOne
    @JoinColumn(name = "prestamo_id")
    private Prestamo prestamo;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    private LocalDateTime fechaAplicacion;

    private LocalDateTime fechaLevantamiento;

    @Builder.Default
    private boolean activa = true;

    private boolean apelada;

    private LocalDateTime fechaApelacion;

    @Column(columnDefinition = "TEXT")
    private String motivoApelacion;

    private Boolean apelacionAceptada;

    @Column(nullable = false, unique = true)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        fechaAplicacion = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }
}