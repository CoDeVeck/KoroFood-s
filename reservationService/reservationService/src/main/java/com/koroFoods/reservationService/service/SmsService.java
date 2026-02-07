package com.koroFoods.reservationService.service;

import org.springframework.stereotype.Service;

import com.koroFoods.reservationService.config.TwilioConfig;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService implements NotificacionService {
    
    private final TwilioConfig twilioConfig;
    
    @Override
    public void enviarCodigoVerificacion(String numeroTelefono, String codigo, String nombreUsuario) {
        try {
            String mensaje = String.format(
                "Hola %s, tu código de verificación es: %s. Válido por 15 minutos.",
                nombreUsuario, codigo
            );
            
            Message message = Message.creator(
                new PhoneNumber(numeroTelefono),
                new PhoneNumber(twilioConfig.getPhoneNumber()),
                mensaje
            ).create();
            
            log.info("SMS enviado exitosamente a: {}. SID: {}", numeroTelefono, message.getSid());
            
        } catch (Exception e) {
            log.error("Error al enviar SMS a {}: {}", numeroTelefono, e.getMessage());
            throw new RuntimeException("Error al enviar el SMS de verificación", e);
        }
    }
}