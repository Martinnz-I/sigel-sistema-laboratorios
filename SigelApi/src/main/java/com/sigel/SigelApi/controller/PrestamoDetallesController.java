package com.sigel.SigelApi.controller;

import com.sigel.SigelApi.service.PrestamoDetalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prestamo-detalles")
@RequiredArgsConstructor
public class PrestamoDetallesController {
    private final PrestamoDetalleService prestamoDetalleService;
}