package com.sigel.SigelApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "estadisticas_diarias", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticaDiaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private LocalDate fecha;

    @Column(name = "total_prestamos", nullable = false)
    @Builder.Default
    private Integer totalPrestamos = 0;

    @Column(name = "total_devoluciones", nullable = false)
    @Builder.Default
    private Integer totalDevoluciones = 0;

    @Column(name = "prestamos_a_tiempo", nullable = false)
    @Builder.Default
    private Integer prestamos_a_tiempo = 0;

    @Column(name = "prestamos_tarde", nullable = false)
    @Builder.Default
    private Integer prestamos_tarde = 0;

    @Column(name = "equipos_disponibles", nullable = false)
    @Builder.Default
    private Integer equiposDisponibles = 0;

    @Column(name = "equipos_en_uso", nullable = false)
    @Builder.Default
    private Integer equiposEnUso = 0;

    @Column(name = "equipos_en_mantenimiento", nullable = false)
    @Builder.Default
    private Integer equiposEnMantenimiento = 0;

    @Column(name = "tickets_abiertoss", nullable = false)
    @Builder.Default
    private Integer ticketsAbiertos = 0;

    @Column(name = "tickets_cerrados", nullable = false)
    @Builder.Default
    private Integer ticketsCerrados = 0;

    @Column(name = "usuariosActivos", nullable = false)
    @Builder.Default
    private Integer usuariosActivos = 0;

    @Column(name = "tiempo_promedio_uso_minutos", nullable = false)
    private Integer tiempoPromedioUsoMinutos;

    @Column(name = "tiempo_promedio_respuesta_tickets_horas", nullable = false)
    private Integer tiempoPromedioRespuestaTicketsHoras;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        fecha = LocalDate.now();
    }
}