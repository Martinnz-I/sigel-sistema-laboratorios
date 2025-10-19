package com.sigel.SigelApi.model;

import com.sigel.SigelApi.util.MapToJsonConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "ticket_comentarios", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketComentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comentario;

    @Column(name = "es_interno", nullable = false)
    @Builder.Default
    private Boolean esInterno = false;

    @Convert(converter = MapToJsonConverter.class)
    @Column(columnDefinition = "JSONB", nullable = false)
    @Builder.Default
    private Map<String, Object> adjuntos = new HashMap<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}