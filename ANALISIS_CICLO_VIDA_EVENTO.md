# 📊 Análisis Profundo del Ciclo de Vida del Evento - SGIE

## 🔴 PROBLEMAS IDENTIFICADOS

### 1. **Datos Hardcodeados en Frontend**

#### EventMontagePage.tsx
- ❌ **Tipos de mesa hardcodeados**: `['Redonda', 'Rectangular', 'Imperial']`
- ❌ **Tipos de silla hardcodeados**: `['Tiffany', 'Crossback', 'Napoleón']`
- ❌ **Tipos de mantel hardcodeados**: `['Lino premium', 'Algodón clásico', 'Raso ceremonial']`
- ❌ **Tipos de sobremantel hardcodeados**: `['Organza', 'Encaje', 'Satinado']`
- ❌ **Paleta de colores hardcodeada**: 5 colores de mantel + 5 colores de sobremantel
- ❌ **Infraestructura hardcodeada**: 4 items (mesa ponque, mesa regalos, espacio músicos, espacio bombas)
- ❌ **Adicionales hardcodeados**: 5 items con precios (Tarimas $180k, Audiovisuales $450k, etc.)
- ❌ **Vajilla hardcodeada**: "Vajilla blanca con ribete dorado"
- ❌ **Sin conexión con API**: No carga ni guarda datos reales

#### EventQuotePage.tsx
- ❌ **Items de cotización hardcodeados**: 9 items con precios específicos
- ❌ **Menú hardcodeado**: Carpaccio, Crema de espárragos, Medallón de lomo, etc.
- ❌ **Montaje hardcodeado**: "12 mesas redondas, sillas Tiffany, mantel lino premium"
- ❌ **Historial de cotizaciones hardcodeado**: 2 cotizaciones falsas (COT-041-02, COT-041-01)
- ❌ **Sin conexión con API**: No carga ni guarda datos reales
- ❌ **Usa función obsoleta**: `getEventSummaryById()` que ya no tiene datos

### 2. **Inconsistencias en el Flujo de Datos**

#### Problema: Datos desconectados
```
EventRequestPage (✅ API real)
    ↓
EventSummaryPage (✅ API real)
    ↓
EventMenuPage (⚠️ Parcial - carga evento pero no guarda menú)
    ↓
EventMontagePage (❌ 100% hardcodeado)
    ↓
EventQuotePage (❌ 100% hardcodeado)
    ↓
EventPaymentsPage (✅ API real)
```

#### Problema: Falta de persistencia
- El menú se puede agregar localmente pero no se guarda en el backend
- El montaje no se conecta con el backend en absoluto
- La cotización no se genera desde el backend

### 3. **Endpoints Faltantes en el Backend**

#### Menús
- ❌ `GET /catalogos/platos` - Listar platos disponibles
- ❌ `GET /catalogos/momentos-menu` - Listar momentos (Entrada, Plato fuerte, etc.)
- ✅ `GET /reservas/{reservaRaizId}/menu` - Existe
- ✅ `PUT /reservas/{reservaRaizId}/menu` - Existe

#### Montajes
- ✅ `GET /reservas/{reservaRaizId}/montaje` - Existe (según types.ts)
- ✅ `PUT /reservas/{reservaRaizId}/montaje` - Existe (según types.ts)
- ❌ Faltan endpoints de catálogos de montaje

#### Cotizaciones
- ✅ `POST /reservas/{reservaId}/cotizaciones` - Existe
- ✅ `GET /cotizaciones/{id}` - Existe
- ✅ `PATCH /cotizaciones/{id}/items/{itemId}` - Existe

---

## 🎯 CICLO DE VIDA CORRECTO DEL EVENTO

### Fase 1: Creación del Evento ✅
**Página**: `EventRequestPage.tsx`
**Estado**: FUNCIONAL

```
1. Usuario selecciona cliente (búsqueda en tiempo real)
2. Usuario define fecha, hora, duración, tipo de evento, tipo de comida
3. Usuario selecciona salón
4. Sistema crea evento con estado PENDIENTE
5. Sistema crea reserva de salón asociada
6. Navega a EventMenuPage
```

**API Calls**:
- `POST /api/eventos` → Crea evento
- `POST /api/eventos/{eventoId}/reservas` → Crea reserva

---

### Fase 2: Configuración del Menú ⚠️
**Página**: `EventMenuPage.tsx`
**Estado**: PARCIALMENTE FUNCIONAL

**Flujo Actual (Incorrecto)**:
```
1. ❌ Carga evento desde API
2. ❌ Muestra formulario para agregar platos manualmente
3. ❌ Guarda localmente (no persiste en backend)
```

**Flujo Correcto (Propuesto)**:
```
1. ✅ Cargar evento desde API
2. ✅ Cargar platos disponibles desde catálogo
3. ✅ Cargar momentos de menú desde catálogo
4. ✅ Intentar cargar menú existente (si ya fue configurado)
5. Usuario selecciona platos por momento
6. Usuario define cantidades y observaciones
7. Usuario define excepciones alimentarias
8. ✅ Guardar menú en backend
9. Navega a EventMontagePage
```

**API Calls Necesarias**:
- `GET /api/catalogos/platos` → Listar platos (FALTA)
- `GET /api/catalogos/momentos-menu` → Listar momentos (FALTA)
- `GET /api/reservas/{reservaRaizId}/menu` → Cargar menú existente
- `PUT /api/reservas/{reservaRaizId}/menu` → Guardar menú

---

### Fase 3: Configuración del Montaje ❌
**Página**: `EventMontagePage.tsx`
**Estado**: NO FUNCIONAL (100% hardcodeado)

**Flujo Actual (Incorrecto)**:
```
1. ❌ Usa datos hardcodeados para todo
2. ❌ No carga catálogos desde API
3. ❌ No guarda en backend
```

**Flujo Correcto (Propuesto)**:
```
1. ✅ Cargar evento desde API
2. ✅ Cargar catálogos de montaje:
   - Tipos de mesa
   - Tipos de silla
   - Manteles (con colores)
   - Sobremanteles (con colores)
   - Tipos de adicionales
3. ✅ Intentar cargar montaje existente (si ya fue configurado)
4. Usuario configura:
   - Mesas (tipo, cantidad, personas por mesa)
   - Sillas (tipo)
   - Textiles (mantel, sobremantel, colores)
   - Vajilla (boolean)
   - Fajón (boolean)
   - Infraestructura (checkboxes)
   - Adicionales (con cantidades)
5. ✅ Guardar montaje en backend
6. Navega a EventQuotePage
```

**API Calls Necesarias**:
- `GET /api/catalogos/tipos-mesa` → Ya existe ✅
- `GET /api/catalogos/tipos-silla` → Ya existe ✅
- `GET /api/catalogos/manteles` → Ya existe ✅
- `GET /api/catalogos/sobremanteles` → Ya existe ✅
- `GET /api/catalogos/colores` → Ya existe ✅
- `GET /api/catalogos/tipos-adicional` → Ya existe ✅
- `GET /api/reservas/{reservaRaizId}/montaje` → Existe
- `PUT /api/reservas/{reservaRaizId}/montaje` → Existe

---

### Fase 4: Generación de Cotización ❌
**Página**: `EventQuotePage.tsx`
**Estado**: NO FUNCIONAL (100% hardcodeado)

**Flujo Actual (Incorrecto)**:
```
1. ❌ Muestra items hardcodeados
2. ❌ No se genera desde menú + montaje
3. ❌ No guarda en backend
```

**Flujo Correcto (Propuesto)**:
```
1. ✅ Cargar evento desde API
2. ✅ Obtener reserva vigente
3. ✅ Generar cotización desde backend (automática):
   - Items de menú (desde configuración de menú)
   - Items de montaje (desde configuración de montaje)
   - Alquiler de salón
4. ✅ Cargar cotización generada
5. Usuario puede:
   - Ajustar precios individuales (solo en estado BORRADOR)
   - Definir porcentaje de anticipo
   - Agregar observaciones
6. Usuario puede:
   - Guardar borrador
   - Enviar cotización (cambia estado a ENVIADA)
   - Registrar aceptación (cambia estado a ACEPTADA)
7. Sistema actualiza estado del evento según estado de cotización
```

**API Calls Necesarias**:
- `POST /api/reservas/{reservaId}/cotizaciones` → Generar cotización
- `GET /api/cotizaciones/{id}` → Obtener cotización
- `PATCH /api/cotizaciones/{id}/items/{itemId}` → Ajustar precio
- `POST /api/cotizaciones/{id}/enviar` → Enviar cotización (FALTA)
- `POST /api/cotizaciones/{id}/aceptar` → Aceptar cotización (FALTA)

---

### Fase 5: Registro de Pagos ✅
**Página**: `EventPaymentsPage.tsx`
**Estado**: FUNCIONAL

```
1. ✅ Cargar evento desde API
2. ✅ Obtener cotización vigente
3. ✅ Mostrar total, pagado, saldo
4. Usuario registra anticipos/abonos
5. ✅ Guardar pago en backend
6. Sistema actualiza estado del evento cuando se completa el pago
```

**API Calls**:
- `POST /api/cotizaciones/{cotizacionId}/anticipos` → Registrar anticipo

---

### Fase 6: Confirmación del Evento ✅
**Página**: `EventSummaryPage.tsx`
**Estado**: FUNCIONAL

```
1. ✅ Cargar evento desde API
2. ✅ Mostrar resumen completo
3. ✅ Mostrar progreso del ciclo de vida
4. Usuario puede confirmar evento
5. ✅ Sistema cambia estado a CONFIRMADO
```

**API Calls**:
- `POST /api/eventos/{eventoId}/confirmar` → Confirmar evento

---

## 📋 ESTADOS DEL EVENTO

```
PENDIENTE
  ↓ (menú configurado)
COTIZACION_ENVIADA
  ↓ (cotización aceptada)
COTIZACION_APROBADA
  ↓ (anticipo registrado)
PENDIENTE_ANTICIPO
  ↓ (pago completo)
CONFIRMADO
```

---

## 🔧 PLAN DE CORRECCIÓN

### Prioridad 1: Conectar Montaje con API
1. Eliminar todos los datos hardcodeados de `EventMontagePage.tsx`
2. Cargar catálogos desde API (tipos mesa, silla, manteles, colores, adicionales)
3. Implementar carga de montaje existente
4. Implementar guardado de montaje

### Prioridad 2: Conectar Cotización con API
1. Eliminar todos los datos hardcodeados de `EventQuotePage.tsx`
2. Implementar generación automática de cotización desde backend
3. Implementar carga de cotización existente
4. Implementar ajuste de precios
5. Implementar cambios de estado (enviar, aceptar)

### Prioridad 3: Completar Menú
1. Agregar endpoints de catálogos de platos y momentos en backend
2. Conectar `EventMenuPage.tsx` con catálogos
3. Implementar guardado real de menú

### Prioridad 4: Validaciones y Transiciones
1. Validar que no se pueda ir a montaje sin menú configurado
2. Validar que no se pueda generar cotización sin montaje configurado
3. Validar transiciones de estado del evento
4. Agregar mensajes de error claros

---

## 📊 RESUMEN DE ESTADO ACTUAL

| Componente | Datos Hardcodeados | Conectado API | Funcional |
|------------|-------------------|---------------|-----------|
| EventRequestPage | ❌ Ninguno | ✅ Sí | ✅ 100% |
| EventSummaryPage | ❌ Ninguno | ✅ Sí | ✅ 100% |
| EventMenuPage | ❌ Ninguno | ⚠️ Parcial | ⚠️ 60% |
| EventAgendaPage | ❌ Ninguno | ❌ Local | ✅ 100% (local) |
| EventMontagePage | ✅ TODO | ❌ No | ❌ 0% |
| EventQuotePage | ✅ TODO | ❌ No | ❌ 0% |
| EventPaymentsPage | ❌ Ninguno | ✅ Sí | ✅ 100% |

**Progreso General**: 51% funcional con API real

---

## 🎯 PRÓXIMOS PASOS RECOMENDADOS

1. **Inmediato**: Limpiar datos hardcodeados de montaje y cotización
2. **Corto plazo**: Conectar montaje con API existente
3. **Mediano plazo**: Conectar cotización con API existente
4. **Largo plazo**: Agregar endpoints faltantes de catálogos de menú
