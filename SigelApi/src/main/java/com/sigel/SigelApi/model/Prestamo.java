package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.LoanStatus;
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
@Table(name = "prestamos", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "autorizado_por")
    private Usuario autorizador;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivo;

    @Column(length = 100, nullable = false)
    private String materia;

    @ManyToOne
    @JoinColumn(name = "maestro_responsable_id")
    private Usuario maestroResponsable;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_retiro")
    private LocalDateTime fechaRetiro;

    @Column(name = "fecha_devolucion_estimada", nullable = false)
    private LocalDateTime fechaDevolcionEstimada;

    @Column(name = "fecha_devolucion_real")
    private LocalDateTime fechaDevolucionReal;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "sigel.loan_status", nullable = false)
    @Builder.Default
    private LoanStatus estado = LoanStatus.activo;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "inspeccion_previa", columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private Map<String, Object> inspeccionPrevia = new HashMap<>();

    @Column(name = "foto_previa_url", length = 255, nullable = false)
    private String fotoPreviaUrl;

    @Column(name = "estado_previo", columnDefinition = "TEXT", nullable = false)
    private String estadoPrevio;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "inspeccion_posterior", columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private Map<String, Object> inspeccionPosterior = new HashMap<>();

    @Column(name = "foto_posterior_url", length = 255)
    private String fotoPosteriorUrl;

    @Column(name = "estado_posterior", columnDefinition = "TEXT")
    private String estadoPosterior;

    @Column(name = "funciono_correctamente")
    private Boolean funcionoCorrectamente;

    @Column(name = "firma_retiro", columnDefinition = "TEXT")
    private String firmaRetiro;

    @Column(name = "firma_devolucion", columnDefinition = "TEXT")
    private String firmaDevolucion;

    @Column(name = "pin_retiro", length = 255)
    private String pinRetiro;

    @Column(name = "pin_devolucion", length = 255)
    private String pinDevolucion;

    @Column(name = "a_tiempo")
    private Boolean aTiempo;

    @Column(name = "minutos_retraso")
    private Integer minutosRetraso;

    @Column(name = "recordatorios_enviados")
    @Builder.Default
    private Integer recordatoriosEnviados = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        fechaSolicitud = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}