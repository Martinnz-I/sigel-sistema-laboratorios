package com.sigel.SigelApi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "accesorios", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accesorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean obligatorio = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer cantidad = 0;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCrate() {
        createdAt = LocalDateTime.now();
    }
}