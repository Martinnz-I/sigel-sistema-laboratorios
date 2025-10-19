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
@Table(name = "tickets", schema = "sigel")
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
    @Column(columnDefinition = "sigel.severity_level", nullable = false)
    private SeverityLevel severidad;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "sigel.ticket_status", nullable = false)
    @Builder.Default
    private TicketStatus estado = TicketStatus.abierto;

    @Column(name = "ocurrio_durante_uso")
    private Boolean ocurrioDuranteUso;

    @ManyToOne
    @JoinColumn(name = "prestamo_id")
    private Prestamo prestamo;

    @Column(nullable = false)
    @Builder.Default
    private Integer prioridad = 3;

    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private Map<String, String> fotos = new HashMap<>();

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "solucion_aplicada", columnDefinition = "TEXT")
    private String solucionAplicada;

    @ManyToOne
    @JoinColumn(name = "mantenimiento_id")
    private Mantenimiento mantenimiento;

    @Column(name = "fecha_reporte", nullable = false, updatable = false)
    private LocalDateTime fechaReporte;

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_inicio_atencion")
    private LocalDateTime fechaInicioAtencion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "tiempo_respuesta_horas")
    private Integer tiempoRespuestaHoras;

    @Column(name = "tiempo_resolucion_horas")
    private Integer tiempoResolucionHoras;

    private Integer calificacion;

    @Column(name = "comentario_usuario", columnDefinition = "TEXT")
    private String comentarioUsuario;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "cerrado_at")
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