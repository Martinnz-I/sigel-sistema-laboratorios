package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.EstadoEquipoPrestamo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "prestamo_detalles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrestamoDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @Enumerated(EnumType.STRING)
    private EstadoEquipoPrestamo estadoAlRecibir;

    private String fotoAlRecibirUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String observacionesAlRecibir;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    @Builder.Default
    private Map<String, Object> checklistRecibir = new HashMap<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaInspeccionRecibir;

    @Enumerated(EnumType.STRING)
    private EstadoEquipoPrestamo estadoAlDevolver;

    private String fotoAlDevolverUrl;

    @Column(columnDefinition = "TEXT")
    private String observacionesAlDevolver;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    @Builder.Default
    private Map<String, Object> checklistDevolver = new HashMap<>();

    private boolean funcionoCorrectamente;

    private LocalDateTime fechaInspeccionDevolver;

    private boolean devuelto;

    private LocalDateTime fechaDevolucion;

    @Column(columnDefinition = "TEXT")
    private String observacionesMaestro;

    @PrePersist
    protected void onCreate() {
        fechaInspeccionRecibir = LocalDateTime.now();
    }
}