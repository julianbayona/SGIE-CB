package com.ejemplo.monolitomodular.notificaciones.aplicacion.servicio;

import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.CrearNotificacionCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarWhatsAppCommand;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.EnviarWhatsAppResult;
import com.ejemplo.monolitomodular.notificaciones.aplicacion.dto.NotificacionView;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.EstadoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.Notificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.modelo.TipoNotificacion;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.NotificacionRepository;
import com.ejemplo.monolitomodular.notificaciones.dominio.puerto.salida.WhatsAppPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificacionApplicationServiceTest {

    @Test
    void deberiaCrearYProcesarNotificacionPendiente() {
        NotificacionRepositoryStub repository = new NotificacionRepositoryStub();
        WhatsAppPortStub whatsAppPort = new WhatsAppPortStub(true);
        NotificacionApplicationService service = new NotificacionApplicationService(repository, whatsAppPort);

        NotificacionView creada = service.ejecutar(command(LocalDateTime.now().minusMinutes(1)));
        int procesadas = service.procesarPendientes(10);

        assertEquals(EstadoNotificacion.PENDIENTE, creada.estado());
        assertEquals(1, procesadas);
        assertEquals(1, whatsAppPort.envios());
        assertEquals(EstadoNotificacion.ENVIADA, repository.ultima().getEstado());
        assertEquals(1, repository.ultima().getIntentos());
    }

    @Test
    void deberiaMarcarErrorCuandoWhatsappFalla() {
        NotificacionRepositoryStub repository = new NotificacionRepositoryStub();
        WhatsAppPortStub whatsAppPort = new WhatsAppPortStub(false);
        NotificacionApplicationService service = new NotificacionApplicationService(repository, whatsAppPort);
        service.ejecutar(command(LocalDateTime.now().minusMinutes(1)));

        int procesadas = service.procesarPendientes(10);

        assertEquals(0, procesadas);
        assertEquals(1, whatsAppPort.envios());
        assertEquals(EstadoNotificacion.ERROR, repository.ultima().getEstado());
        assertEquals(1, repository.ultima().getIntentos());
    }

    @Test
    void deberiaReintentarSoloDestinatariosNoEnviados() {
        NotificacionRepositoryStub repository = new NotificacionRepositoryStub();
        WhatsAppParcialPortStub whatsAppPort = new WhatsAppParcialPortStub();
        NotificacionApplicationService service = new NotificacionApplicationService(repository, whatsAppPort);
        service.ejecutar(new CrearNotificacionCommand(
                UUID.randomUUID(),
                TipoNotificacion.PRUEBA_PLATO_CLIENTE,
                LocalDateTime.now().minusMinutes(1),
                "{\"mensaje\":\"Prueba\"}",
                List.of(
                        new CrearNotificacionCommand.Destinatario(null, "573001112233"),
                        new CrearNotificacionCommand.Destinatario(null, "573004445566")
                )
        ));

        int primerIntento = service.procesarPendientes(10);
        whatsAppPort.permitirTelefono("573004445566");
        int segundoIntento = service.procesarPendientes(10);

        assertEquals(0, primerIntento);
        assertEquals(1, segundoIntento);
        assertEquals(1, whatsAppPort.enviosPorTelefono("573001112233"));
        assertEquals(2, whatsAppPort.enviosPorTelefono("573004445566"));
        assertEquals(EstadoNotificacion.ENVIADA, repository.ultima().getEstado());
        assertEquals(2, repository.ultima().getIntentos());
    }

    @Test
    void noDeberiaProcesarNotificacionProgramadaAFuturo() {
        NotificacionRepositoryStub repository = new NotificacionRepositoryStub();
        WhatsAppPortStub whatsAppPort = new WhatsAppPortStub(true);
        NotificacionApplicationService service = new NotificacionApplicationService(repository, whatsAppPort);
        service.ejecutar(command(LocalDateTime.now().plusDays(1)));

        int procesadas = service.procesarPendientes(10);

        assertEquals(0, procesadas);
        assertEquals(0, whatsAppPort.envios());
        assertEquals(EstadoNotificacion.PENDIENTE, repository.ultima().getEstado());
    }

    private static CrearNotificacionCommand command(LocalDateTime fechaProgramada) {
        return new CrearNotificacionCommand(
                UUID.randomUUID(),
                TipoNotificacion.PRUEBA_PLATO_CLIENTE,
                fechaProgramada,
                "{\"mensaje\":\"Prueba\"}",
                List.of(new CrearNotificacionCommand.Destinatario(null, "573001112233"))
        );
    }

    private static class NotificacionRepositoryStub implements NotificacionRepository {

        private final List<Notificacion> notificaciones = new ArrayList<>();

        @Override
        public Notificacion guardar(Notificacion notificacion) {
            notificaciones.removeIf(actual -> actual.getId().equals(notificacion.getId()));
            notificaciones.add(notificacion);
            return notificacion;
        }

        @Override
        public List<Notificacion> buscarPendientes(LocalDateTime fechaReferencia, int limite) {
            return notificaciones.stream()
                    .filter(notificacion -> !notificacion.getFechaProgramada().isAfter(fechaReferencia))
                    .filter(notificacion -> notificacion.getIntentos() < 3)
                    .filter(notificacion -> notificacion.getEstado() == EstadoNotificacion.PENDIENTE
                            || notificacion.getEstado() == EstadoNotificacion.ERROR)
                    .sorted(Comparator.comparing(Notificacion::getFechaProgramada))
                    .limit(limite)
                    .toList();
        }

        Notificacion ultima() {
            return notificaciones.get(notificaciones.size() - 1);
        }
    }

    private static class WhatsAppPortStub implements WhatsAppPort {

        private final boolean exitoso;
        private int envios;

        private WhatsAppPortStub(boolean exitoso) {
            this.exitoso = exitoso;
        }

        @Override
        public EnviarWhatsAppResult enviar(EnviarWhatsAppCommand command) {
            envios++;
            return exitoso ? EnviarWhatsAppResult.ok() : EnviarWhatsAppResult.error("Fallo simulado");
        }

        int envios() {
            return envios;
        }
    }

    private static class WhatsAppParcialPortStub implements WhatsAppPort {

        private final Map<String, Integer> enviosPorTelefono = new HashMap<>();
        private final List<String> telefonosPermitidos = new ArrayList<>(List.of("573001112233"));

        @Override
        public EnviarWhatsAppResult enviar(EnviarWhatsAppCommand command) {
            enviosPorTelefono.merge(command.telefono(), 1, Integer::sum);
            if (telefonosPermitidos.contains(command.telefono())) {
                return EnviarWhatsAppResult.ok();
            }
            return EnviarWhatsAppResult.error("Fallo simulado");
        }

        void permitirTelefono(String telefono) {
            telefonosPermitidos.add(telefono);
        }

        int enviosPorTelefono(String telefono) {
            return enviosPorTelefono.getOrDefault(telefono, 0);
        }
    }
}
