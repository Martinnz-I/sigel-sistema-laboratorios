package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.LoanStatus;
import com.sigel.SigelApi.enums.TipoPrestamo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prestamos")
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
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "autorizado_por")
    private Usuario autorizador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "maestro_responsable_id", nullable = false)
    private Usuario maestroResponsable;

    @ManyToOne
    @JoinColumn(name = "sesion_laboratorio_id")
    private SesionLaboratorio sesionLaboratorio;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TipoPrestamo tipoPrestamo = TipoPrestamo.EXTERNO;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String motivo;

    @Column(length = 100, nullable = false)
    private String materia;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    private LocalDateTime fechaRetiro;

    private boolean sinFechaLimite;

    private LocalDateTime fechaDevolucionEstimada;

    private LocalDateTime fechaDevolucionReal;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LoanStatus estado = LoanStatus.ACTIVO;

    private Boolean aTiempo;

    private Integer minutosRetraso;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "prestamo", fetch = FetchType.LAZY)
    private List<PrestamoDetalle> detalles = new ArrayList<>();

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