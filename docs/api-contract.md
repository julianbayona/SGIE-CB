# Contrato API SGIE Club Boyaca

Este documento resume los endpoints principales que debe consumir el frontend para ejecutar el flujo operativo del SGIE. Todos los endpoints bajo `/api/**` requieren JWT, excepto `/api/auth/login`.

## Convenciones

- Base local: `http://localhost:8080`
- Base Railway: `https://sgie-cb-production.up.railway.app`
- Autenticacion: header `Authorization: Bearer <accessToken>`
- Fechas: formato ISO local, ejemplo `2026-08-10T18:00:00`
- El frontend no envia `usuarioId`; el backend lo toma del JWT.
- Errores de dominio retornan JSON con `timestamp` y `message`.

## Autenticacion

### Login

`POST /api/auth/login`

```json
{
  "nombre": "Administrador",
  "contrasena": "admin123"
}
```

Respuesta:

```json
{
  "tokenType": "Bearer",
  "accessToken": "jwt",
  "expiresAt": "2026-05-05T12:00:00Z",
  "usuarioId": "uuid",
  "nombre": "Administrador",
  "rol": "ADMINISTRADOR"
}
```

### Usuario autenticado

`GET /api/auth/me`

## Clientes

### Crear cliente

`POST /api/clientes`

```json
{
  "cedula": "100000001",
  "nombreCompleto": "Cliente Demo",
  "telefono": "573001112233",
  "correo": "cliente.demo@example.com",
  "tipoCliente": "NO_SOCIO"
}
```

### Consultar clientes

- `GET /api/clientes`
- `GET /api/clientes/{clienteId}`

## Catalogos necesarios para el flujo

### Tipos de evento

`POST /api/catalogos/tipos-evento`

```json
{
  "nombre": "Boda",
  "descripcion": "Evento social"
}
```

### Tipos de comida

`POST /api/catalogos/tipos-comida`

```json
{
  "nombre": "Cena",
  "descripcion": "Servicio de cena"
}
```

### Salon

`POST /api/salones`

```json
{
  "nombre": "Salon Principal",
  "capacidad": 120,
  "descripcion": "Salon para eventos sociales"
}
```

Disponibilidad:

`GET /api/salones/disponibilidad?fechaHoraInicio=2026-08-10T18:00:00&fechaHoraFin=2026-08-11T02:00:00&capacidadMinima=80`

### Catalogos de montaje

Endpoints CRUD:

- `/api/catalogos/colores`
- `/api/catalogos/manteles`
- `/api/catalogos/sobremanteles`
- `/api/catalogos/tipos-mesa`
- `/api/catalogos/tipos-silla`
- `/api/catalogos/tipos-adicional`

Ejemplo `tipo_adicional`:

```json
{
  "nombre": "Sonido",
  "modoCobro": "SERVICIO",
  "precioBase": 180000
}
```

`modoCobro` acepta `UNIDAD` o `SERVICIO`.

## Evento y reservas

### Crear evento

`POST /api/eventos`

```json
{
  "clienteId": "uuid",
  "tipoEventoId": "uuid",
  "tipoComidaId": "uuid",
  "fechaHoraInicio": "2026-08-10T18:00:00",
  "fechaHoraFin": "2026-08-11T02:00:00"
}
```

Estado inicial: `PENDIENTE`.

### Crear reserva de salon

`POST /api/eventos/{eventoId}/reservas`

```json
{
  "salonId": "uuid",
  "numInvitados": 80,
  "fechaHoraInicio": "2026-08-10T18:00:00",
  "fechaHoraFin": "2026-08-11T02:00:00"
}
```

Respuesta importante:

```json
{
  "id": "uuid-version-vigente",
  "reservaRaizId": "uuid-raiz",
  "version": 1,
  "vigente": true
}
```

El frontend debe conservar `reservaRaizId` para consultar o actualizar la reserva. Cuando se modifica una reserva, el backend crea una nueva version vigente y conserva la anterior como snapshot historico.

### Actualizar reserva por snapshot

`PATCH /api/eventos/reservas/{reservaRaizId}`

Usa el mismo cuerpo de crear reserva. Si existe cotizacion activa de la reserva anterior, queda `DESACTUALIZADA` y el evento vuelve a estado pendiente de cotizacion cuando aplica.

## Menu

### Guardar menu de una reserva

`PUT /api/reservas/{reservaRaizId}/menu`

```json
{
  "notasGenerales": "Sin mani",
  "selecciones": [
    {
      "tipoMomentoId": "uuid",
      "items": [
        {
          "platoId": "uuid",
          "cantidad": 80,
          "excepciones": "2 vegetarianos"
        }
      ]
    }
  ]
}
```

### Consultar menu

`GET /api/reservas/{reservaRaizId}/menu`

Nota tecnica: actualmente no existen endpoints REST para crear `plato`, `tipo_momento_menu` ni `plato_momento`. Para probar el flujo completo se deben cargar esos datos por SQL o implementar luego esos CRUD.

## Montaje

### Guardar montaje de una reserva

`PUT /api/reservas/{reservaRaizId}/montaje`

```json
{
  "observaciones": "Montaje formal",
  "mesas": [
    {
      "tipoMesaId": "uuid",
      "tipoSillaId": "uuid",
      "sillaPorMesa": 8,
      "cantidadMesas": 10,
      "mantelId": "uuid",
      "sobremantelId": null,
      "vajilla": true,
      "fajon": false
    }
  ],
  "infraestructura": {
    "mesaPonque": true,
    "mesaRegalos": true,
    "espacioMusicos": false,
    "estanteBombas": false
  },
  "adicionales": [
    {
      "tipoAdicionalId": "uuid",
      "cantidad": 1
    }
  ]
}
```

### Consultar montaje

`GET /api/reservas/{reservaRaizId}/montaje`

## Cotizaciones

### Crear cotizacion borrador desde reserva vigente

`POST /api/reservas/{reservaRaizId}/cotizaciones`

```json
{
  "descuento": 0,
  "observaciones": "Cotizacion inicial"
}
```

La cotizacion toma automaticamente los items desde `menu` y `montaje`.

### Consultas

- `GET /api/cotizaciones/{cotizacionId}`
- `GET /api/reservas/{reservaRaizId}/cotizacion-vigente`
- `GET /api/eventos/{eventoId}/cotizaciones`
- `GET /api/cotizaciones/{cotizacionId}/documento`

### Estados

- `PATCH /api/cotizaciones/{cotizacionId}/generar`
- `PATCH /api/cotizaciones/{cotizacionId}/enviar`
- `POST /api/cotizaciones/{cotizacionId}/enviar-email`
- `PATCH /api/cotizaciones/{cotizacionId}/aceptar`
- `PATCH /api/cotizaciones/{cotizacionId}/rechazar`

Una cotizacion se puede aceptar desde `GENERADA` o `ENVIADA`.

### Actualizar precios negociados

`PUT /api/cotizaciones/{cotizacionId}/items`

```json
{
  "items": [
    {
      "itemId": "uuid",
      "precioOverride": 25000
    }
  ]
}
```

Si la cotizacion esta `BORRADOR`, se edita la misma. Si esta `GENERADA` o `ENVIADA`, se crea una nueva cotizacion `BORRADOR` y la anterior queda `DESACTUALIZADA`.

## Pagos y anticipos

### Registrar anticipo

`POST /api/cotizaciones/{cotizacionId}/anticipos`

```json
{
  "valor": 300000,
  "metodoPago": "TRANSFERENCIA",
  "fechaPago": "2026-05-05",
  "observaciones": "Pago inicial"
}
```

### Consultas

- `GET /api/cotizaciones/{cotizacionId}/anticipos`
- `GET /api/eventos/{eventoId}/estado-financiero`

El saldo se calcula por evento, acumulando anticipos aunque haya nuevas versiones de reserva o nuevas cotizaciones.

### Recordatorio manual de anticipo

`POST /api/eventos/{eventoId}/recordatorios-anticipo`

```json
{
  "fechaRecordatorio": "2026-05-06"
}
```

Procesamiento manual:

`POST /api/recordatorios-anticipo/procesar-pendientes?limite=50`

## Prueba de plato, Calendar y email

### Crear prueba de plato

`POST /api/eventos/{eventoId}/pruebas-plato`

```json
{
  "fechaRealizacion": "2026-08-08T11:00:00"
}
```

Al crear la prueba, los observers registran:

- Notificaciones para cliente y personal.
- Evento pendiente para Google Calendar.

Los schedulers procesan posteriormente el envio de email y la sincronizacion con Calendar.

### Reintentar Calendar

`POST /api/calendario/eventos/{eventoCalendarId}/reintentar`

## Confirmacion de evento

`POST /api/eventos/{eventoId}/confirmar`

Requisitos actuales:

- Debe existir cotizacion vigente aceptada para una reserva vigente del evento.
- No debe existir solapamiento con reservas vigentes de eventos `CONFIRMADO`.

Al confirmar, los observers crean notificaciones y eventos de Calendar asociados al evento confirmado.

## Flujo frontend recomendado

1. Login y guardar `accessToken`.
2. Crear o seleccionar cliente.
3. Crear o seleccionar catalogos basicos: tipo de evento, tipo de comida y salon.
4. Crear evento.
5. Crear una o varias reservas de salon.
6. Configurar menu por `reservaRaizId`.
7. Configurar montaje por `reservaRaizId`.
8. Crear cotizacion desde la reserva.
9. Revisar items y ajustar precios negociados si aplica.
10. Generar documento de cotizacion.
11. Enviar cotizacion por email si aplica.
12. Aceptar o rechazar cotizacion.
13. Registrar anticipos y consultar saldo del evento.
14. Confirmar evento cuando el negocio lo decida.
15. Programar prueba de plato si aplica.

## Brechas actuales del contrato

- Falta CRUD REST para `plato`, `tipo_momento_menu` y la asociacion `plato_momento`.
- Falta endpoint general de monitoreo de notificaciones por evento.
- Falta endpoint de consulta de `evento_calendar` por evento/origen para monitorear sincronizacion desde frontend.
