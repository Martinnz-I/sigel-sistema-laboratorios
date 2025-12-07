package com.sigel.SigelApi.service;

import com.sigel.SigelApi.enums.EstadoSesionLab;
import com.sigel.SigelApi.enums.LoanStatus;
import com.sigel.SigelApi.model.Prestamo;
import com.sigel.SigelApi.model.SesionLaboratorio;
import com.sigel.SigelApi.repository.PrestamoRepository;
import com.sigel.SigelApi.repository.SesionLaboratorioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinalizacionSesionService {
    private final PrestamoRepository prestamoRepository;
    private final SesionLaboratorioRepository sesionRepository;

    @Transactional
    public void finalizarSesionConPrestamos(SesionLaboratorio sesion) {
        // Mueve aquí la lógica de finalizarSesionAutomaticamente
        sesion.setEstado(EstadoSesionLab.FINALIZADA);
        sesion.setFechaFinReal(LocalDateTime.now());

        String notaAuto = String.format(
                "\n⚠️ FINALIZACIÓN AUTOMÁTICA: " +
                        "Sesión finalizada automáticamente por exceder tiempo estimado + 30 min de gracia. " +
                        "Hora estimada de fin: %s, Hora real de fin: %s",
                sesion.getFechaFinEstimada(),
                LocalDateTime.now()
        );

        sesion.setNotas((sesion.getNotas() != null ? sesion.getNotas() : "") + notaAuto);

        sesionRepository.save(sesion);

        List<Prestamo> prestamos = prestamoRepository.findBySesionLaboratorioIdAndEstado(
                sesion.getId(), LoanStatus.ACTIVO
        );

        LocalDateTime ahora = LocalDateTime.now();
        for(Prestamo prestamo : prestamos) {
            prestamo.setFechaDevolucionReal(ahora);
            prestamo.setEstado(LoanStatus.VENCIDO);
            prestamo.setATiempo(false);
            prestamo.setMinutosRetraso(
                    (int) Duration.between(prestamo.getFechaDevolucionEstimada(), ahora).toMinutes()
            );
        }

        prestamoRepository.saveAll(prestamos);
    }
}