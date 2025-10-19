package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservaciones", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservacion {
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

    @Column(name = "fecha_reservacion", nullable = false)
    private LocalDateTime fechaReservacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "sigel.reservation_status", nullable = false)
    @Builder.Default
    private ReservationStatus estado = ReservationStatus.pendiente;

    @Column(name = "recordatorios_enviados", nullable = false)
    @Builder.Default
    private Integer recordatoriosENviados = 0;

    @Column(name = "tiempo_gracia_minutos")
    @Builder.Default
    private Integer tiempoGraciaMinutos = 15;

    @OneToOne
    @JoinColumn(name = "prestamo_id")
    private Prestamo prestamo;

    @ManyToOne
    @JoinColumn(name = "cancelada_por")
    private Usuario canceladaPor;

    @Column(name = "motivo_cancelacion", columnDefinition = "TEXT")
    private String motivoCancelacion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "cancelada_at")
    private LocalDateTime canceladaAt;

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