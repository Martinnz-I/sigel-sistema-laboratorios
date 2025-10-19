package com.sigel.SigelApi.model;

import com.sigel.SigelApi.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones", schema = "sigel")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "sigel.notification_type", nullable = false)
    private NotificationType tipo;

    @Column(length = 200, nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Column(nullable = false)
    @Builder.Default
    private Integer prioridad = 3;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "action_url", length = 255)
    private String actionUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean leida = false;

    @Column(name = "leida_at")
    private LocalDateTime leidaAt;

    @Column(name = "enviada_push", nullable = false)
    @Builder.Default
    private Boolean enviadaPush = false;

    @Column(name = "enviada_email", nullable = false)
    @Builder.Default
    private Boolean enviadaEmail = false;

    @Column(name = "enviada_sms", nullable = false)
    @Builder.Default
    private Boolean enviadaSms = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expira_at")
    private LocalDateTime expiraAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}