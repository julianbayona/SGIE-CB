# 📋 Diseño de Pruebas de Integración — Club Boyacá

> **Proyecto:** Sistema de Gestión de Eventos — Club Boyacá  
> **Versión:** v1  
> **Tipo de pruebas:** Integración  
> **Total de casos:** 31  
> **Estado general:** Pendiente de ejecución

---

## Índice

1. [Gestión de Clientes](#1-gestión-de-clientes)
2. [Gestión de Salones](#2-gestión-de-salones)
3. [Disponibilidad de Salones](#3-disponibilidad-de-salones)
4. [Catálogos del Sistema](#4-catálogos-del-sistema)
5. [Gestión de Eventos](#5-gestión-de-eventos)
6. [Reservas de Salones](#6-reservas-de-salones)
7. [Consulta de Eventos](#7-consulta-de-eventos)
8. [Manejo Transversal de Errores](#8-manejo-transversal-de-errores)
9. [Persistencia — Capa JPA](#9-persistencia--capa-jpa)

---

## 1. Gestión de Clientes

---

### PI001 — Crear cliente exitosamente desde REST hasta persistencia

| Campo | Detalle |
|---|---|
| **ID** | PI001 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_CLIENTES-02 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ClienteController`
- `ClienteApplicationService`
- `ClienteRepository`
- `UsuarioRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existe un usuario creador válido en el sistema (o el campo es opcional según configuración).
- La cédula `"123"` no existe previamente en el repositorio.

**Pasos:**
1. Enviar petición REST de creación de cliente.
2. El controller construye `RegistrarClienteCommand`.
3. El service valida usuario creador y unicidad de cédula.
4. El repository guarda el agregado `Cliente`.
5. El controller retorna `ClienteResponse`.

**Especificación de entrada:**
`RegistrarClienteRequest` con cédula `"123"`, nombre `"Ana Pérez"`, teléfono, correo, tipo `SOCIO` y usuario creador válido/opcional.

**Resultado esperado:**
- El cliente queda persistido, activo por defecto y con ID generado.
- La respuesta contiene los datos enviados.

**Validaciones clave:**
- Conversión `Request → Command`.
- Existencia del usuario creador si aplica.
- Unicidad de cédula.
- Guardado del cliente y transformación `View → Response`.

---

### PI002 — Crear cliente con cédula duplicada

| Campo | Detalle |
|---|---|
| **ID** | PI002 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_CLIENTES-02 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ClienteController`
- `ClienteApplicationService`
- `ClienteRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existe un cliente previamente registrado con cédula `"123"`.

**Pasos:**
1. Crear cliente inicial con cédula `"123"` (válida).
2. Enviar nueva petición REST con la misma cédula `"123"`.
3. Verificar que el service rechaza la operación.
4. Verificar respuesta de error estándar.

**Especificación de entrada:**
Registrar dos clientes con la misma cédula `"123"`.

**Resultado esperado:**
- Se retorna error HTTP 400 por cédula duplicada.
- El repositorio conserva únicamente el cliente inicial.

**Validaciones clave:**
- El service consulta `buscarPorCedula()`, detecta duplicidad y lanza `DomainException`.
- El segundo cliente no debe persistirse.

---

### PI003 — Crear cliente con usuario creador inexistente

| Campo | Detalle |
|---|---|
| **ID** | PI003 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_CLIENTES-02 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ClienteController`
- `ClienteApplicationService`
- `UsuarioRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- El `usuarioCreadorId` enviado no existe en el sistema.

**Pasos:**
1. Enviar petición REST con `usuarioCreadorId` aleatorio/inexistente.
2. Validar que el service intenta resolver el usuario.
3. Validar que no se invoca el método de guardado del cliente.
4. Validar que la respuesta es manejada por `ApiExceptionHandler`.

**Especificación de entrada:**
`RegistrarClienteRequest` con `usuarioCreadorId` aleatorio/no existente.

**Resultado esperado:**
- Respuesta HTTP 400.
- El cliente no se crea.

**Validaciones clave:**
- El service consulta `UsuarioRepository`, no encuentra el usuario y lanza `DomainException`.

---

### PI004 — Listar clientes sin filtro

| Campo | Detalle |
|---|---|
| **ID** | PI004 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_CLIENTES-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ClienteController`
- `ClienteApplicationService`
- `ClienteRepository`

**Precondiciones:**
- Existen varios clientes persistidos en el sistema.

**Pasos:**
1. Preparar varios clientes persistidos.
2. Consumir endpoint de listado sin parámetro `q`.
3. Verificar que se invoca el método `listar()`.
4. Validar cantidad y datos retornados.

**Especificación de entrada:**
`GET /clientes` sin parámetro `q`.

**Resultado esperado:**
- Se retorna la lista completa de clientes registrados como `List<ClienteResponse>`.

**Validaciones clave:**
- El controller debe invocar `listar()` cuando `q` está vacío.
- Las views deben transformarse correctamente a responses.

---

### PI005 — Buscar clientes por filtro textual

| Campo | Detalle |
|---|---|
| **ID** | PI005 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_CLIENTES-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ClienteController`
- `ClienteApplicationService`
- `ClienteRepository`

**Precondiciones:**
- Existen clientes con nombres, cédulas y teléfonos distintos.

**Pasos:**
1. Preparar clientes con nombres, cédulas y teléfonos distintos.
2. Consumir endpoint con parámetro `q`.
3. Verificar que solo aparecen coincidencias.
4. Repetir la consulta con un filtro sin resultados esperados.

**Especificación de entrada:**
`GET /clientes?q=Ana` o con cédula/teléfono parcial.

**Resultado esperado:**
- Se retornan únicamente los clientes coincidentes con el filtro.
- Si no hay coincidencias, la lista queda vacía.

**Validaciones clave:**
- El controller decide entre `buscar(q)` y `listar()`.
- El repository aplica `buscarPorFiltro()`.

---

### PI006 — Obtener cliente inexistente por ID

| Campo | Detalle |
|---|---|
| **ID** | PI006 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_CLIENTES-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ClienteController`
- `ClienteApplicationService`
- `ClienteRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- El UUID enviado no corresponde a ningún cliente registrado.

**Pasos:**
1. Consumir endpoint `obtenerPorId` con UUID aleatorio/inexistente.
2. Verificar la consulta al repositorio.
3. Verificar que se lanza `DomainException`.
4. Validar estructura de error HTTP en la respuesta.

**Especificación de entrada:**
`GET /clientes/{uuid}` con UUID aleatorio/no existente.

**Resultado esperado:**
- El sistema no retorna cliente.
- Se responde con error HTTP 400 controlado.

**Validaciones clave:**
- El service consulta `buscarPorId()`, detecta ausencia y delega el error al `ApiExceptionHandler`.

---

## 2. Gestión de Salones

---

### PI007 — Crear salón exitosamente

| Campo | Detalle |
|---|---|
| **ID** | PI007 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-05 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `SalonController`
- `SalonApplicationService`
- `SalonRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- No existe un salón con el nombre `"Salón República"` en el sistema.

**Pasos:**
1. Enviar petición REST de creación de salón.
2. El controller convierte el request a command.
3. El service valida que el nombre sea único.
4. El repository guarda el salón.
5. Se retorna respuesta con encabezado `Location`.

**Especificación de entrada:**
`RegistrarSalonRequest` con nombre `"Salón República"`, capacidad `120` y descripción.

**Resultado esperado:**
- El salón queda persistido, activo y disponible para consultas posteriores.
- La respuesta incluye `SalonResponse` con los datos y `Location`.

**Validaciones clave:**
- `Request → Command`.
- `existePorNombre()`.
- Construcción de `Salon`, persistencia y respuesta.

---

### PI008 — Crear salón con nombre duplicado

| Campo | Detalle |
|---|---|
| **ID** | PI008 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-05 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `SalonController`
- `SalonApplicationService`
- `SalonRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existe un salón previamente registrado.

**Pasos:**
1. Crear el salón inicial exitosamente.
2. Enviar una nueva petición con el mismo nombre o variante en mayúsculas/minúsculas.
3. Verificar el rechazo por `DomainException`.
4. Validar que no se guarda el duplicado.

**Especificación de entrada:**
Registrar dos salones con nombre equivalente variando capitalización.

**Resultado esperado:**
- Respuesta HTTP 400.
- El segundo registro es rechazado por nombre duplicado.

**Validaciones clave:**
- El service debe usar `existePorNombre()` validando duplicidad case-insensitive.

---

### PI009 — Crear salón con capacidad inválida

| Campo | Detalle |
|---|---|
| **ID** | PI009 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-05 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `SalonController`
- `SalonApplicationService`
- `SalonRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Ninguna.

**Pasos:**
1. Enviar petición con capacidad `0` o valor negativo.
2. Validar que el service rechaza la creación.
3. Verificar que no se invoca el método de guardado.
4. Validar respuesta HTTP de error.

**Especificación de entrada:**
`RegistrarSalonRequest` con capacidad `0` o negativa.

**Resultado esperado:**
- Respuesta HTTP 400.
- El salón no queda persistido.

**Validaciones clave:**
- Reglas de validación de capacidad mínima y manejo transversal de `DomainException`.

---

## 3. Disponibilidad de Salones

---

### PI010 — Consultar disponibilidad excluyendo salones ocupados

| Campo | Detalle |
|---|---|
| **ID** | PI010 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `SalonController`
- `SalonApplicationService`
- `SalonRepository`
- `ReservaSalonRepository`

**Precondiciones:**
- Existen salones activos e inactivos en el sistema.
- Al menos un salón tiene una reserva vigente en el rango horario consultado.

**Pasos:**
1. Preparar salones activos/inactivos y reservas en el rango horario.
2. Consultar disponibilidad con rango válido.
3. Verificar que `ReservaSalonRepository` retorna los IDs de salones ocupados.
4. Validar que el resultado está correctamente filtrado.

**Especificación de entrada:**
`fechaHoraInicio`, `fechaHoraFin` y `capacidadMinima` válidos, con al menos un salón ocupado y uno libre.

**Resultado esperado:**
- Listado de salones disponibles: activos, con capacidad suficiente y sin reserva vigente en el rango.

**Validaciones clave:**
- Validación de rango de fechas.
- `buscarSalonesOcupados()`.
- Filtro por estado activo, capacidad y exclusión de salones ocupados.

---

### PI011 — Consultar disponibilidad con filtros múltiples

| Campo | Detalle |
|---|---|
| **ID** | PI011 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `SalonController`
- `SalonApplicationService`
- `SalonRepository`
- `ReservaSalonRepository`

**Precondiciones:**
- Existen tres salones con capacidades de 40, 80 y 120.
- El salón de 120 tiene una reserva vigente en el rango de consulta.

**Pasos:**
1. Preparar tres salones con capacidades distintas (40, 80 y 120).
2. Crear reserva vigente para el salón de capacidad 120.
3. Consultar disponibilidad con `capacidadMinima=80`.
4. Validar que el salón de capacidad exacta (80) aparece si está libre.

**Especificación de entrada:**
Rango horario válido, `capacidadMinima=80`, salones de 40/80/120 y una reserva ocupando el de 120.

**Resultado esperado:**
- Lista con salones disponibles de capacidad >= 80, excluyendo el ocupado.

**Validaciones clave:**
- Capacidad exacta se acepta como suficiente.
- El salón ocupado se excluye independientemente de la capacidad.
- Se aplican simultáneamente: filtro de capacidad, estado activo y ocupación.

---

### PI012 — Consultar disponibilidad con fechas inválidas

| Campo | Detalle |
|---|---|
| **ID** | PI012 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `SalonController`
- `SalonApplicationService`
- `ApiExceptionHandler`

**Precondiciones:**
- Ninguna.

**Pasos:**
1. Enviar consulta con `fechaHoraInicio` nula o `fechaHoraFin` nula.
2. Repetir con `fechaHoraFin` igual o anterior a `fechaHoraInicio`.
3. Repetir con `capacidadMinima` igual a 0 o negativa.
4. Verificar `DomainException` en cada caso.
5. Confirmar respuesta de error estándar.

**Especificación de entrada:**
Combinaciones de: `fechaHoraInicio` nula, `fechaHoraFin` nula, fin igual/anterior a inicio o `capacidadMinima <= 0`.

**Resultado esperado:**
- Respuesta HTTP 400 en todos los casos.
- No se procesa la consulta de disponibilidad.

**Validaciones clave:**
- Rechazo temprano de rango inválido y capacidad mínima inválida antes de consultar repositorios.

---

## 4. Catálogos del Sistema

---

### PI013 — Crear catálogo de tipo de evento exitosamente

| Campo | Detalle |
|---|---|
| **ID** | PI013 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-07 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `TipoEventoController`
- `CatalogoApplicationService`
- `TipoEventoRepository`

**Precondiciones:**
- No existe un tipo de evento con el nombre `"Boda"`.

**Pasos:**
1. Enviar request al controller de tipo de evento.
2. El controller convierte el request a command.
3. El service valida que el nombre esté disponible.
4. El repository persiste la entidad con el repositorio específico.
5. Se retorna el response con la entidad creada.

**Especificación de entrada:**
Request REST para crear tipo de evento con nombre `"Boda"` y descripción.

**Resultado esperado:**
- El tipo de evento queda creado, activo y disponible para crear eventos.

**Validaciones clave:**
- Validar nombre disponible.
- Creación de entidad de dominio, persistencia y transformación a response.

---

### PI014 — Crear color con nombre duplicado

| Campo | Detalle |
|---|---|
| **ID** | PI014 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-MONTAJE_EVENTO-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ColorController`
- `CatalogoApplicationService`
- `ColorRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existe un color con nombre `"Azul"` previamente registrado.

**Pasos:**
1. Crear color inicial con nombre `"Azul"`.
2. Enviar segundo request con nombre `"azul"` (diferente capitalización).
3. Verificar el rechazo del service.
4. Validar respuesta de error HTTP.

**Especificación de entrada:**
Crear color `"Azul"` y luego `"azul"`.

**Resultado esperado:**
- Respuesta HTTP 400.
- No se crea el color duplicado.

**Validaciones clave:**
- Validación de duplicidad case-insensitive del nombre de color.

---

### PI015 — Crear mantel con color inactivo

| Campo | Detalle |
|---|---|
| **ID** | PI015 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-MONTAJE_EVENTO-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `MantelController`
- `CatalogoApplicationService`
- `ColorRepository`
- `MantelRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existe un color en estado inactivo en el sistema.

**Pasos:**
1. Preparar color existente en estado inactivo.
2. Enviar request de creación de mantel con ese `colorId`.
3. Validar que se lanza `DomainException`.
4. Confirmar que `MantelRepository` no persiste el mantel.

**Especificación de entrada:**
`CatalogoConColorCommand`/request con `colorId` asociado a color inactivo.

**Resultado esperado:**
- Respuesta HTTP 400.
- El mantel no queda persistido.

**Validaciones clave:**
- El service resuelve el color mediante `ColorRepository` y rechaza la operación si está inactivo.

---

## 5. Gestión de Eventos

---

### PI016 — Crear evento sin reservas iniciales

| Campo | Detalle |
|---|---|
| **ID** | PI016 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-02 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `ClienteRepository`
- `TipoEventoRepository`
- `TipoComidaRepository`
- `UsuarioRepository`
- `EventoRepository`
- `HistorialEstadoEventoRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existe un cliente activo registrado.
- Existe un tipo de evento activo.
- Existe un tipo de comida activo.
- Existe un usuario creador válido.
- Las fechas `fechaHoraInicio` y `fechaHoraFin` son válidas (inicio anterior a fin).

**Pasos:**
1. Enviar `CrearEventoRequest`.
2. El service valida todas las relaciones requeridas.
3. Se valida que `fechaHoraInicio < fechaHoraFin`.
4. Se guarda el `Evento`.
5. Se registra el historial de creación.
6. Se retorna `EventoView`.

**Especificación de entrada:**
`CrearEventoRequest` con cliente existente, tipo de evento activo, tipo de comida activo, usuario creador válido y fechas válidas.

**Resultado esperado:**
- El evento queda persistido en estado `PENDIENTE`.
- El historial de creación queda registrado.

**Validaciones clave:**
- Relaciones con cliente, tipo evento, tipo comida y usuario.
- Validación de rango temporal.
- Guardado del evento e historial.

---

### PI017 — Crear evento con cliente inexistente

| Campo | Detalle |
|---|---|
| **ID** | PI017 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-INTEGRIDAD-01 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `ClienteRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- El `clienteId` enviado no corresponde a ningún cliente registrado.

**Pasos:**
1. Enviar request con `clienteId` aleatorio/inexistente.
2. Verificar la consulta a `ClienteRepository`.
3. Verificar que se lanza `DomainException`.
4. Validar que no se guarda el evento ni el historial.

**Especificación de entrada:**
`CrearEventoRequest` con `clienteId` aleatorio/no existente.

**Resultado esperado:**
- Respuesta HTTP 400.
- El evento no queda creado.

**Validaciones clave:**
- `ClienteRepository` no encuentra el cliente y el service detiene el flujo.

---

### PI018 — Crear evento con catálogo inactivo

| Campo | Detalle |
|---|---|
| **ID** | PI018 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-07 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `TipoEventoRepository`
- `TipoComidaRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existe un cliente y un usuario válidos.
- Existe al menos un tipo de evento o tipo de comida en estado inactivo.

**Pasos:**
1. Preparar cliente y usuario válidos.
2. Seleccionar tipo de evento o tipo de comida en estado inactivo.
3. Enviar request de creación de evento.
4. Validar el rechazo y la ausencia de persistencia.

**Especificación de entrada:**
`CrearEventoRequest` con `tipoEventoId` inactivo o `tipoComidaId` inactivo.

**Resultado esperado:**
- Respuesta HTTP 400.
- El evento no queda creado.

**Validaciones clave:**
- El tipo de evento y el tipo de comida deben existir y estar activos para poder crear el evento.

---

### PI019 — Crear evento con fechas inválidas

| Campo | Detalle |
|---|---|
| **ID** | PI019 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-02 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `ApiExceptionHandler`

**Precondiciones:**
- Ninguna.

**Pasos:**
1. Enviar request con `fechaHoraInicio` nula.
2. Repetir con `fechaHoraFin` nula.
3. Repetir con `fechaHoraFin` no posterior a `fechaHoraInicio`.
4. Verificar la validación del service en cada caso.
5. Validar que no se guarda el `Evento`.
6. Validar respuesta de error.

**Especificación de entrada:**
`CrearEventoRequest` con `fechaHoraInicio` nula, `fechaHoraFin` nula o `fin` no posterior a inicio.

**Resultado esperado:**
- Respuesta HTTP 400 en todos los casos.
- El sistema rechaza eventos con fechas nulas o rango inválido.

**Validaciones clave:**
- Reglas de rango temporal se validan antes de persistir.

---

## 6. Reservas de Salones

---

### PI020 — Crear reserva de salón para evento

| Campo | Detalle |
|---|---|
| **ID** | PI020 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-05 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `EventoRepository`
- `UsuarioRepository`
- `SalonRepository`
- `ReservaSalonRepository`

**Precondiciones:**
- Existe un evento creado previamente.
- Existe un salón activo sin conflictos en el rango horario solicitado.
- Existe un usuario válido.

**Pasos:**
1. Preparar evento y salón existente.
2. Enviar request de creación de reserva.
3. El service busca el evento, usuario y salón.
4. Se consulta existencia de conflicto (debe retornar `false`).
5. Se guarda la reserva.
6. Se retorna el evento con reservas actualizadas.

**Especificación de entrada:**
`CrearReservaSalonRequest` con `eventoId` existente, `usuarioId` válido, `salonId` existente, cantidad de invitados y rango horario válido sin conflicto.

**Resultado esperado:**
- La reserva queda asociada al evento y aparece como vigente en la respuesta.

**Validaciones clave:**
- Existencia de evento, usuario y salón.
- Validación de fechas.
- `existeConflicto()` debe retornar `false`.
- Guardado de la reserva.

---

### PI021 — Crear reserva con salón inexistente

| Campo | Detalle |
|---|---|
| **ID** | PI021 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-05 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `SalonRepository`
- `ReservaSalonRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existen un evento y un usuario válidos.
- El `salonId` enviado no corresponde a ningún salón registrado.

**Pasos:**
1. Preparar evento y usuario válidos.
2. Enviar request con `salonId` aleatorio/inexistente.
3. Verificar el rechazo del service.
4. Validar que `ReservaSalonRepository` no guarda la reserva.

**Especificación de entrada:**
`CrearReservaSalonRequest` con `salonId` aleatorio/no existente.

**Resultado esperado:**
- Respuesta HTTP 400.
- La reserva no queda creada.

**Validaciones clave:**
- `SalonRepository.buscarTodosPorIds()` no retorna el salón solicitado.

---

### PI022 — Crear reserva con conflicto de ocupación

| Campo | Detalle |
|---|---|
| **ID** | PI022 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `ReservaSalonRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existe una reserva vigente para el salón en el rango 18:00–22:00.

**Pasos:**
1. Preparar reserva vigente para el salón entre 18:00 y 22:00.
2. Enviar nueva reserva que se solape con ese rango.
3. Verificar la llamada a `existeConflicto()`.
4. Validar el rechazo y la ausencia de nuevo registro.

**Especificación de entrada:**
`CrearReservaSalonRequest` para un salón con reserva vigente solapada en el mismo rango.

**Resultado esperado:**
- Respuesta HTTP 400.
- La nueva reserva no se crea.

**Validaciones clave:**
- `existeConflicto()` debe detectar el solapamiento de fechas del salón.

---

### PI023 — Modificar reserva vigente creando nueva versión

| Campo | Detalle |
|---|---|
| **ID** | PI023 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-05 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `ReservaSalonRepository`
- `EventoRepository`
- `UsuarioRepository`
- `SalonRepository`

**Precondiciones:**
- Existe un evento con una reserva vigente.
- El salón de destino está disponible en el nuevo rango horario.

**Pasos:**
1. Preparar evento con reserva vigente.
2. Enviar request de modificación con nuevo rango/salón.
3. Buscar reserva por `reservaRaizId`.
4. Verificar conflictos excluyendo la raíz actual.
5. Desactivar la versión vigente actual.
6. Crear nueva versión de la reserva.
7. Retornar evento actualizado.

**Especificación de entrada:**
`ModificarReservaSalonRequest` con `reservaRaizId` vigente, `usuarioId` válido, `salonId` destino existente, cantidad de invitados y nuevo rango válido sin conflicto.

**Resultado esperado:**
- La reserva queda versionada: la anterior inactiva y la nueva vigente con versión incrementada.

**Validaciones clave:**
- Búsqueda de reserva vigente por raíz.
- Existencia de evento, usuario y salón.
- Exclusión del conflicto de la raíz actual en la consulta.
- Versionamiento correcto.

---

### PI024 — Modificar reserva con raíz inexistente

| Campo | Detalle |
|---|---|
| **ID** | PI024 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-05 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `ReservaSalonRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- El `reservaRaizId` enviado no corresponde a ninguna reserva vigente.

**Pasos:**
1. Enviar request de modificación con `reservaRaizId` aleatorio/inexistente.
2. Verificar la consulta a `ReservaSalonRepository`.
3. Validar que se lanza `DomainException`.
4. Confirmar que no se desactiva ni se crea ninguna reserva.

**Especificación de entrada:**
`ModificarReservaSalonRequest` con `reservaRaizId` aleatorio/no existente.

**Resultado esperado:**
- Respuesta HTTP 400.
- No se modifica ninguna reserva.

**Validaciones clave:**
- `buscarVigentePorRaizId()` no encuentra reserva vigente y el service lanza `DomainException`.

---

### PI025 — Modificar reserva con conflicto contra otra reserva

| Campo | Detalle |
|---|---|
| **ID** | PI025 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-NO_FUNCIONALES-03 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `ReservaSalonRepository`
- `ApiExceptionHandler`

**Precondiciones:**
- Existen dos reservas vigentes para diferentes rangos/salones.

**Pasos:**
1. Preparar dos reservas vigentes.
2. Intentar modificar una reserva hacia el rango/salón ocupado por la otra.
3. Verificar la detección de conflicto por `existeConflicto()`.
4. Validar que la versión vigente original se conserva sin cambios.

**Especificación de entrada:**
`ModificarReservaSalonRequest` que mueve la reserva a un salón/rango ocupado por otra reserva vigente.

**Resultado esperado:**
- Respuesta HTTP 400.
- La versión vigente original se conserva intacta.

**Validaciones clave:**
- `existeConflicto()` excluye la raíz actual pero detecta conflicto con otra reserva.

---

## 7. Consulta de Eventos

---

### PI026 — Consultar evento por ID con reservas vigentes

| Campo | Detalle |
|---|---|
| **ID** | PI026 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-01 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `EventoRepository`
- `ReservaSalonRepository`

**Precondiciones:**
- Existe un evento con una o más reservas vigentes.

**Pasos:**
1. Preparar evento con reservas vigentes asociadas.
2. Consumir endpoint `obtenerPorId` con el ID del evento.
3. Verificar la búsqueda del evento en el repositorio.
4. Verificar la carga de reservas vigentes mediante `listarPorEvento()`.
5. Validar la respuesta consolidada.

**Especificación de entrada:**
`GET /eventos/{id}` con ID existente que tiene una o más reservas vigentes.

**Resultado esperado:**
- La respuesta incluye el evento solicitado y únicamente sus reservas vigentes.

**Validaciones clave:**
- El service recupera el evento y luego consulta `listarPorEvento()` para componer `EventoView`.

---

### PI027 — Listar eventos con reservas

| Campo | Detalle |
|---|---|
| **ID** | PI027 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-GESTION_EVENTOS-01 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoController`
- `EventoApplicationService`
- `EventoRepository`
- `ReservaSalonRepository`

**Precondiciones:**
- Existen varios eventos, algunos con reservas vigentes y otros sin ellas.

**Pasos:**
1. Preparar varios eventos en el sistema.
2. Agregar reservas vigentes a un subconjunto de los eventos.
3. Consumir endpoint de listado.
4. Validar que cada evento incluye sus reservas correspondientes.

**Especificación de entrada:**
`GET /eventos` con eventos existentes, algunos con reservas y otros sin ellas.

**Resultado esperado:**
- Se retorna una lista completa y consistente de eventos.
- Cada evento incluye sus reservas vigentes cuando existan.

**Validaciones clave:**
- Por cada evento se construye `EventoView` con `listarPorEvento()`.

---

## 8. Manejo Transversal de Errores

---

### PI028 — Manejo transversal de DomainException

| Campo | Detalle |
|---|---|
| **ID** | PI028 |
| **Tipo** | Integración |
| **Tipo de integración** | Top-down |
| **Requisito asociado** | DP-NO_FUNCIONALES-01 |
| **Técnica** | Caja negra |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ApiExceptionHandler`
- `DomainException`
- Controllers REST (cualquiera)

**Precondiciones:**
- Existe al menos una condición de error de dominio reproducible.

**Pasos:**
1. Ejecutar una petición REST que dispare `DomainException` en un servicio de aplicación.
2. Verificar que la excepción llega al handler.
3. Validar código HTTP y cuerpo de respuesta.
4. Confirmar que no se retorna una respuesta de éxito.

**Especificación de entrada:**
Cualquier petición REST que cause `DomainException` en un servicio de aplicación.

**Resultado esperado:**
- Los errores de dominio se entregan como HTTP 400 con mensaje controlado.
- No se expone stack trace en la respuesta.

**Validaciones clave:**
- `handleDomainException()` traduce errores de dominio en respuesta HTTP 400 sin exponer detalles internos.

---

## 9. Persistencia — Capa JPA

---

### PI029 — Persistencia de cliente con adaptador JPA

| Campo | Detalle |
|---|---|
| **ID** | PI029 |
| **Tipo** | Integración |
| **Tipo de integración** | Bottom-up |
| **Requisito asociado** | DP-GESTION_CLIENTES-02 |
| **Técnica** | Caja gris |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `ClienteJpaRepositoryAdapter`
- `SpringDataClienteJpaRepository`
- `ClienteJpaEntity`
- `ClienteRepository`

**Precondiciones:**
- Base de datos de prueba disponible y esquema inicializado.

**Pasos:**
1. Construir `Cliente` de dominio con todos sus atributos.
2. Guardar mediante `ClienteJpaRepositoryAdapter`.
3. Consultar por ID, cédula y filtro textual.
4. Validar la equivalencia de los datos reconstruidos.

**Especificación de entrada:**
`Cliente` de dominio con cédula, nombre, teléfono, correo y tipo.

**Resultado esperado:**
- El adaptador persiste y reconstruye `Cliente` sin pérdida de datos.

**Validaciones clave:**
- Traducción `dominio ↔ entidad JPA`.
- `guardar()`, `buscarPorId()`, `buscarPorCedula()` y `buscarPorFiltro()`.

---

### PI030 — Persistencia de salones y ocupación

| Campo | Detalle |
|---|---|
| **ID** | PI030 |
| **Tipo** | Integración |
| **Tipo de integración** | Bottom-up |
| **Requisito asociado** | DP-GESTION_EVENTOS-03 |
| **Técnica** | Caja gris |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `SalonJpaRepositoryAdapter`
- `ReservaSalonJpaRepositoryAdapter`
- `SpringDataSalonJpaRepository`
- `ReservaSalonRepository`

**Precondiciones:**
- Base de datos de prueba disponible y esquema inicializado.

**Pasos:**
1. Persistir salones de prueba (activos e inactivos).
2. Persistir reservas vigentes asociadas a los salones.
3. Consultar salones ocupados por rango horario.
4. Verificar que solo se retornan los IDs de los salones ocupados.

**Especificación de entrada:**
Salones activos/inactivos y reservas vigentes en un rango horario.

**Resultado esperado:**
- La capa JPA entrega datos suficientes para que el service calcule la disponibilidad correctamente.

**Validaciones clave:**
- `guardar salones`, `listar salones`, `buscarTodosPorIds()` y `buscarSalonesOcupados()`.

---

### PI031 — Persistencia de evento, historial y reservas vigentes

| Campo | Detalle |
|---|---|
| **ID** | PI031 |
| **Tipo** | Integración |
| **Tipo de integración** | Bottom-up |
| **Requisito asociado** | DP-GESTION_EVENTOS-02 |
| **Técnica** | Caja gris |
| **Estado** | Pendiente |

**Módulos involucrados:**
- `EventoJpaRepositoryAdapter`
- `ReservaSalonJpaRepositoryAdapter`
- `HistorialEstadoEventoJpaRepositoryAdapter`
- `EventoRepository`
- `ReservaSalonRepository`
- `HistorialEstadoEventoRepository`

**Precondiciones:**
- Base de datos de prueba disponible y esquema inicializado.

**Pasos:**
1. Persistir `Evento` de dominio.
2. Guardar historial de estado de creación.
3. Guardar reserva vigente asociada al evento.
4. Desactivar versión vigente y crear nueva versión de la reserva.
5. Consultar `listarPorEvento()` y verificar consistencia.

**Especificación de entrada:**
`Evento` de dominio, historial de estado y reservas con versión vigente/inactiva.

**Resultado esperado:**
- La persistencia conserva evento, historial y versionamiento de reservas de forma consistente.
- `listarPorEvento()` retorna únicamente las reservas vigentes.

**Validaciones clave:**
- `guardar()`, `buscarPorId()`, `listar()`.
- Guardar historial.
- `listarPorEvento()` y `desactivarReservaVigente()`.

---

## Resumen por módulo

| Módulo | IDs | Total |
|---|---|---|
| Gestión de Clientes | PI001 – PI006 | 6 |
| Gestión de Salones | PI007 – PI009 | 3 |
| Disponibilidad de Salones | PI010 – PI012 | 3 |
| Catálogos del Sistema | PI013 – PI015 | 3 |
| Gestión de Eventos | PI016 – PI019 | 4 |
| Reservas de Salones | PI020 – PI025 | 6 |
| Consulta de Eventos | PI026 – PI027 | 2 |
| Manejo Transversal de Errores | PI028 | 1 |
| Persistencia — Capa JPA | PI029 – PI031 | 3 |
| **Total** | | **31** |
