package com.sigel.SigelApi.service.implementation;

import com.sigel.SigelApi.model.Notificacion;

public interface NotificacionImpl {
    Notificacion buscarNotificacionPorId(Long notificacionId);

    Notificacion guardarNotificacion(Notificacion notificacion);
}