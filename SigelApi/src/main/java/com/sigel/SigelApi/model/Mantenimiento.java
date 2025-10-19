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
@Table(name = "mantenimientos", schema = "sigel")
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

    @Column(name = "descripcion_problema", columnDefinition = "TEXT", nullable = false)
    private String descripcionProblema;

    @Column(name = "descripcion_solucion", columnDefinition = "TEXT")
    private String descripcionSolucion;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "duracion_dias")
    private Integer duracionDias;

    @Column(name = "costo_mano_obra", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal costoManoObra = BigDecimal.ZERO;

    @Column(name = "costo_refacciones", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal costoRefacciones = BigDecimal.ZERO;

    @Column(name = "costo_total", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal costoTotal = BigDecimal.ZERO;

    private Boolean exitoso;

    @Column(name = "requiere_seguimiento", nullable = false)
    @Builder.Default
    private Boolean requiereSeguimiento = false;

    @Column(name = "proximo_mantenimiento")
    private LocalDate proximoMantenimiento;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "fotos_antes", columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private Map<String, String> fotosAntes = new HashMap<>();

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "fotos_despues", columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private Map<String, String> fotos_despues = new HashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, updatable = false)
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