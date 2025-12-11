package com.sigel.SigelApi.scheduler;

import com.sigel.SigelApi.service.SesionLaboratorioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SesionLaboratorioScheduler {
    private final SesionLaboratorioService service;

    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void autoFinalizarSesionesVencidas() {
        System.out.println("Ejecutando inspección sesión laboratorio cada 15 Minutos");
        service.autoFinalizarSesionesVencidas();
    }
}