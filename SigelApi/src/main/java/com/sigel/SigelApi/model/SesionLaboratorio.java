package com.sigel.SigelApi.model;


import com.sigel.SigelApi.enums.EquipmentStatus;
import com.sigel.SigelApi.enums.EstadoSesionLab;
import com.sigel.SigelApi.enums.ModoAsignacionLab;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sesiones_laboratorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SesionLaboratorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "maestro_id", nullable = false)
    private Usuario maestro;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(length = 100, nullable = false)
    private String materia;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFinEstimada;

    private LocalDateTime fechaFinReal;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoSesionLab estado = EstadoSesionLab.PROGRAMADA;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ModoAsignacionLab modoAsignacion = ModoAsignacionLab.MANUAL;

    private boolean permiteCambioEquipo;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}