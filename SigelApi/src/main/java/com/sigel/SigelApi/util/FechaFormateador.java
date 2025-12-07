package com.sigel.SigelApi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FechaFormateador {
    public static String formatearFecha(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE d 'de' MMMM 'a las' h:mm a",
                Locale.of("es", "ES")
        );

        String fechaFormateada = fecha.format(formatter);

        // Capitalizar la primera letra del día
        fechaFormateada = fechaFormateada.substring(0, 1).toUpperCase() +
                fechaFormateada.substring(1);

        return fechaFormateada;
    }
}