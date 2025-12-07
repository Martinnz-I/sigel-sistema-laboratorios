package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.EquipmentStatus;
import com.sigel.SigelApi.enums.TipoPrestamo;
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
@Table(name = "equipos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false, unique = true)
    private String codigoQr;

    @Column(length = 200, nullable = false)
    private String nombre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEquipo categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    @Column(length = 100)
    private String modelo;

    @Column(length = 100, unique = true)
    private String numeroSerie;

    @ManyToOne(optional = false)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    @Builder.Default
    private Map<String, Object> especificaciones = new HashMap<>();

    @Builder.Default
    private boolean disponiblePrestamo = true;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TipoPrestamo tipoPrestamo = TipoPrestamo.AMBOS;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    @Builder.Default
    private Map<String, String> fotos = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EquipmentStatus estado = EquipmentStatus.DISPONIBLE;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String estadoFisico;

    @Builder.Default
    private boolean activo = true;

    private int totalPrestamos;

    private int totalHorasUso;

    private int totalReparaciones;

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