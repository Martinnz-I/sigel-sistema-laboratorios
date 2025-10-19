package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.EquipmentStatus;
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
@Table(name = "equipos", schema = "sigel")
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

    @Column(name = "codigo_qr", length = 255, nullable = false, unique = true)
    private String codigoQr;

    @Column(nullable = false, length = 200)
    private String nombre;

    // Clasificación
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEquipo categoria;

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private Marca marca;

    @Column(length = 100)
    private String modelo;

    @Column(name = "numero_serie", length = 100, unique = true)
    private String numeroSerie;

    // Ubicación
    @ManyToOne(optional = false)
    @JoinColumn(name = "laboratorio_id", nullable = false)
    private Laboratorio laboratorio;

    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    // Información financiera
    @Column(name = "valor_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCompra;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Column(name = "numero_factura", length = 100)
    private String numeroFactura;

    @Column(name = "en_garantia")
    @Builder.Default
    private Boolean enGarantia = false;

    @Column(name = "fecha_fin_garantia")
    private LocalDate fechaFinGarantia;

    // Especificaciones técnicas
    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private Map<String, Object> especificaciones = new HashMap<>();

    @Column(name = "voltaje_operacion", length = 20)
    private String voltajeOperacion;

    @Column(name = "consumo_watts", precision = 8, scale = 2)
    private BigDecimal consumoWatts;

    @Column(name = "peso_kg", precision = 8, scale = 2)
    private BigDecimal pesoKg;

    @Column(length = 50)
    private String dimensiones;

    // Documentación
    @Column(name = "manual_url", length = 255)
    private String manualUrl;

    @Column(name = "hoja_datos_url", length = 255)
    private String hojaDatosUrl;

    @Column(name = "foto_principal_url", nullable = false, length = 255)
    private String fotoPrincipalUrl;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "fotos_adicionales", columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private Map<String, String> fotosAdicionales = new HashMap<>();

    // Estado y mantenimiento
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "sigel.equipment_status", nullable = false)
    @Builder.Default
    private EquipmentStatus estado = EquipmentStatus.disponible;

    @Column(name = "estado_fisico", columnDefinition = "TEXT", nullable = false)
    private String estadoFisico;

    @Column(name = "requiere_calibracion")
    @Builder.Default
    private Boolean requiereCalibracion = false;

    @Column(name = "frecuencia_calibracion_dias")
    private Integer frecuenciaCalibracionDias;

    @Column(name = "ultima_calibracion")
    private LocalDate ultimaCalibracion;

    @Column(name = "proxima_calibracion")
    private LocalDate proximaCalibracion;

    // Estadísticas
    @Column(name = "total_prestamos", nullable = false)
    @Builder.Default
    private Integer totalPrestamos = 0;

    @Column(name = "total_horas_uso", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalHorasUso = BigDecimal.ZERO;

    @Column(name = "total_reparaciones", nullable = false)
    @Builder.Default
    private Integer totalReparaciones = 0;

    @Column(name = "costo_total_mantenimiento", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal costoTotalMantenimiento = BigDecimal.ZERO;

    // Control
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(columnDefinition = "TEXT")
    private String notas;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "dado_baja_at")
    private LocalDateTime dadoBajaAt;

    @Column(name = "dado_baja_motivo", columnDefinition = "TEXT")
    private String dadoBajaMotivo;

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