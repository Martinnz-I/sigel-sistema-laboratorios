package com.sigel.SigelApi.model;

import com.sigel.SigelApi.util.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "mantenimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mantenimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", length = 50, nullable = false, unique = true)
    private String codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_mantenimiento_id", nullable = false)
    private TipoMantenimiento tipoMantenimiento;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcionProblema;

    @Column(columnDefinition = "TEXT")
    private String descripcionSolucion;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private Integer duracionDias;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoManoObra = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoRefacciones = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costoTotal = BigDecimal.ZERO;

    private Boolean exitoso;

    private boolean requiereSeguimiento;

    private LocalDate proximoMantenimiento;

    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "JSONB")
    @Builder.Default
    private Map<String, String> fotosAntes = new HashMap<>();

    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "JSONB")
    @Builder.Default
    private Map<String, String> fotosDespues = new HashMap<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
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