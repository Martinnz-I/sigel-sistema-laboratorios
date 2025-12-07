package com.sigel.SigelApi.service;

import com.sigel.SigelApi.exceptions.ResourceNotFoundException;
import com.sigel.SigelApi.model.Notificacion;
import com.sigel.SigelApi.repository.NotificacionRepository;
import com.sigel.SigelApi.service.implementation.NotificacionImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacionService implements NotificacionImpl {
    private final NotificacionRepository repository;

    @Override
    public Notificacion buscarNotificacionPorId(Long notificacionId) {
        return repository.findById(notificacionId).orElseThrow(
                () -> new ResourceNotFoundException("Notificación no encontrada")
        );
    }

    @Override
    public Notificacion guardarNotificacion(Notificacion notificacion) {
        return repository.save(notificacion);
    }


}