package com.sigel.SigelApi.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sigel.SigelApi.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrService {
    private final StorageService storageService;

    public String generarYSubirQR(String codigo) {
        try {
            //String codigo = generarCodigoParaQr();

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(codigo, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutput);
            byte[] qrBytes = pngOutput.toByteArray();

            return storageService.subirImagenDesdeBytes(
                    qrBytes,
                    "qr_" + codigo + ".png",
                    "image/png"
            );

        } catch (WriterException | IOException e) {
            throw new BadRequestException("Error al generar o subir el QR", e);
        }
    }

    private String generarCodigoParaQr() {
        UUID uuid = UUID.randomUUID();
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(uuid.toString().getBytes())
                .substring(0, 12);
    }
}