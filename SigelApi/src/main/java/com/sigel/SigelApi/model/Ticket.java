package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.SeverityLevel;
import com.sigel.SigelApi.enums.TicketStatus;
import com.sigel.SigelApi.util.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reportado_por", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "asignado_a")
    private Usuario tecnicoAsignado;

    @Column(length = 200, nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeverityLevel severidad;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketStatus estado = TicketStatus.abierto;

    private Boolean ocurrioDuranteUso;

    @ManyToOne
    @JoinColumn(name = "prestamo_id")
    private Prestamo prestamo;

    @Builder.Default
    private int prioridad = 3;

    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "JSONB")
    @Builder.Default
    private Map<String, String> fotos = new HashMap<>();

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String solucionAplicada;

    @ManyToOne
    @JoinColumn(name = "mantenimiento_id")
    private Mantenimiento mantenimiento;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaReporte;

    private LocalDateTime fechaAsignacion;

    private LocalDateTime fechaInicioAtencion;

    private LocalDateTime fechaResolucion;

    private Integer tiempoRespuestaHoras;

    private Integer tiempoResolucionHoras;

    private Integer calificacion;

    @Column(columnDefinition = "TEXT")
    private String comentarioUsuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime cerradoAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        fechaReporte = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}