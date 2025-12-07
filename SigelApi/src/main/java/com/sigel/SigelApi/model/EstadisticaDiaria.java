package com.sigel.SigelApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "estadisticas_diarias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticaDiaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    private int totalPrestamos;

    private int totalDevoluciones;

    @Column(name = "prestamos_a_tiempo")
    private int prestamosAtiempo;

    private int prestamos_tarde;

    private int equiposDisponibles;

    private int equiposEnUso;

    private int equiposEnMantenimiento;

    private int ticketsAbiertos;

    private int ticketsCerrados;

    private int usuariosActivos;

    @Column(nullable = false)
    private Integer tiempoPromedioUsoMinutos;

    @Column(nullable = false)
    private Integer tiempoPromedioRespuestaTicketsHoras;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        fecha = LocalDate.now();
    }
}