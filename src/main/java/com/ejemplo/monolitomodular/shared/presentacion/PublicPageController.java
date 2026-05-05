package com.ejemplo.monolitomodular.shared.presentacion;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicPageController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return page(
                "SGIE Club Boyaca",
                """
                        <h1>SGIE Club Boyaca</h1>
                        <p>Sistema de Gestion Integral de Eventos del Club Boyaca.</p>
                        <p>Esta aplicacion es de uso operativo interno para la gestion de clientes, eventos, reservas, cotizaciones, pagos, notificaciones y sincronizacion con Google Calendar.</p>
                        <ul>
                          <li><a href="/privacy">Politica de privacidad</a></li>
                          <li><a href="/terms">Terminos del servicio</a></li>
                        </ul>
                        """
        );
    }

    @GetMapping(value = "/privacy", produces = MediaType.TEXT_HTML_VALUE)
    public String privacy() {
        return page(
                "Politica de privacidad - SGIE Club Boyaca",
                """
                        <h1>Politica de privacidad</h1>
                        <p>Ultima actualizacion: 5 de mayo de 2026.</p>

                        <h2>Responsable</h2>
                        <p>SGIE Club Boyaca es una aplicacion web para la gestion operativa de eventos del Club Boyaca.</p>

                        <h2>Datos que se tratan</h2>
                        <p>La aplicacion puede almacenar datos de clientes, eventos, reservas de salon, menus, montajes, cotizaciones, anticipos, usuarios internos y registros de notificaciones.</p>

                        <h2>Uso de Google Calendar</h2>
                        <p>La integracion con Google Calendar se usa unicamente para crear, actualizar o sincronizar eventos relacionados con pruebas de plato y eventos confirmados. La aplicacion no usa Google Calendar para fines publicitarios ni comparte estos datos con terceros no autorizados.</p>

                        <h2>Uso de correo electronico</h2>
                        <p>La aplicacion puede enviar correos relacionados con pruebas de plato, confirmaciones de evento, cotizaciones y recordatorios de anticipo.</p>

                        <h2>Finalidad</h2>
                        <p>Los datos se usan para administrar el ciclo de vida de eventos, coordinar disponibilidad de salones, generar cotizaciones, registrar pagos y facilitar comunicaciones operativas.</p>

                        <h2>Conservacion</h2>
                        <p>La informacion se conserva para trazabilidad historica y control operativo, de acuerdo con las reglas internas del sistema.</p>

                        <h2>Contacto</h2>
                        <p>Para solicitudes relacionadas con privacidad, contacte al responsable operativo del Club Boyaca o al administrador del sistema.</p>
                        """
        );
    }

    @GetMapping(value = "/terms", produces = MediaType.TEXT_HTML_VALUE)
    public String terms() {
        return page(
                "Terminos del servicio - SGIE Club Boyaca",
                """
                        <h1>Terminos del servicio</h1>
                        <p>Ultima actualizacion: 5 de mayo de 2026.</p>

                        <h2>Uso autorizado</h2>
                        <p>SGIE Club Boyaca es una aplicacion de uso interno para personal autorizado del Club Boyaca. El acceso requiere credenciales asignadas por el administrador del sistema.</p>

                        <h2>Responsabilidades del usuario</h2>
                        <p>Los usuarios deben registrar informacion veraz, proteger sus credenciales y usar el sistema unicamente para actividades relacionadas con la gestion de eventos del Club Boyaca.</p>

                        <h2>Integraciones externas</h2>
                        <p>La aplicacion puede conectarse con Google Calendar y servicios de correo electronico para automatizar tareas operativas. Estas integraciones se usan solo para los procesos propios del sistema.</p>

                        <h2>Disponibilidad</h2>
                        <p>El servicio puede estar sujeto a mantenimientos, cambios tecnicos o indisponibilidad temporal de proveedores externos.</p>

                        <h2>Restricciones</h2>
                        <p>No se permite usar la aplicacion para fines no autorizados, acceso indebido, alteracion de informacion o actividades ajenas a la operacion del Club Boyaca.</p>

                        <h2>Contacto</h2>
                        <p>Para soporte o inquietudes sobre estos terminos, contacte al administrador del sistema.</p>
                        """
        );
    }

    private String page(String title, String body) {
        return """
                <!doctype html>
                <html lang="es">
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s</title>
                  <style>
                    body {
                      margin: 0;
                      font-family: Georgia, "Times New Roman", serif;
                      background: #f7f2e9;
                      color: #1f2a24;
                      line-height: 1.6;
                    }
                    main {
                      max-width: 860px;
                      margin: 0 auto;
                      padding: 48px 24px;
                    }
                    h1 {
                      font-size: 2.4rem;
                      margin-bottom: 0.5rem;
                    }
                    h2 {
                      margin-top: 2rem;
                      color: #6f3f18;
                    }
                    a {
                      color: #7f3f13;
                    }
                    .card {
                      background: #fffdf8;
                      border: 1px solid #e4d8c4;
                      border-radius: 18px;
                      padding: 32px;
                      box-shadow: 0 18px 42px rgba(56, 38, 18, 0.08);
                    }
                  </style>
                </head>
                <body>
                  <main>
                    <section class="card">
                      %s
                    </section>
                  </main>
                </body>
                </html>
                """.formatted(title, body);
    }
}
