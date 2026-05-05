# Monolito modular con Spring Boot

Base de proyecto para trabajar con una arquitectura de **monolito modular** usando **Spring Boot**.  
Cada módulo está organizado con el patrón **por capas**:

- `presentacion`
- `aplicacion`
- `dominio`
- `infraestructura`

## Estructura

```text
src
├── main
│   ├── java/com/ejemplo/monolitomodular
│   │   ├── shared
│   │   ├── clientes
│   │   │   ├── presentacion
│   │   │   ├── aplicacion
│   │   │   ├── dominio
│   │   │   └── infraestructura
│   │   └── productos
│   │       ├── presentacion
│   │       ├── aplicacion
│   │       ├── dominio
│   │       └── infraestructura
│   └── resources
└── test
```

## Criterios arquitectónicos

- Es una sola aplicación Spring Boot, un solo despliegue, una sola base de código.
- Cada módulo encapsula su lógica y expone casos de uso hacia fuera.
- `presentacion` depende de `aplicacion`.
- `aplicacion` coordina casos de uso y depende de puertos del `dominio`.
- `infraestructura` implementa puertos del `dominio`.
- `dominio` no depende de Spring.

## Módulos incluidos

- `clientes`
- `productos`

Ambos están creados como ejemplo y pueden servir de plantilla para nuevos módulos de negocio.

## Cómo ejecutar

```bash
mvn spring-boot:run
```

## Configuracion para despliegue

La aplicacion toma configuracion desde variables de entorno. El archivo `.env.example` contiene los nombres esperados para base de datos, notificaciones y calendario.

Variables principales:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sgie
DB_USERNAME=postgres
DB_PASSWORD=postgres

SGIE_NOTIFICACIONES_PERSONAL_GRUPO_WHATSAPP_ID=grupo-personal@g.us
SGIE_NOTIFICACIONES_PERSONAL_CORREOS=gerente@club.com,tesorero@club.com
SGIE_NOTIFICACIONES_PRUEBA_PLATO_CHEF_TELEFONO=573001111111
SGIE_NOTIFICACIONES_PRUEBA_PLATO_GERENTE_TELEFONO=573002222222
SGIE_NOTIFICACIONES_PRUEBA_PLATO_TESORERO_TELEFONO=573003333333
SGIE_NOTIFICACIONES_PRUEBA_PLATO_CHEF_CORREO=chef@club.com
SGIE_NOTIFICACIONES_PRUEBA_PLATO_GERENTE_CORREO=gerente@club.com
SGIE_NOTIFICACIONES_PRUEBA_PLATO_TESORERO_CORREO=tesorero@club.com

SGIE_EMAIL_ENABLED=false
SGIE_EMAIL_FROM=
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true

SGIE_RECORDATORIOS_ANTICIPO_SCHEDULER_ENABLED=true
SGIE_RECORDATORIOS_ANTICIPO_SCHEDULER_FIXED_DELAY_MS=3600000
SGIE_RECORDATORIOS_ANTICIPO_SCHEDULER_LIMITE=20
SGIE_RECORDATORIOS_ANTICIPO_DIAS_ANTES=7
SGIE_RECORDATORIOS_ANTICIPO_REPETIR_CADA_HORAS=24
SGIE_RECORDATORIOS_ANTICIPO_PROGRAMADOS_SCHEDULER_ENABLED=true
SGIE_RECORDATORIOS_ANTICIPO_PROGRAMADOS_SCHEDULER_CRON=0 0 9 * * *
SGIE_RECORDATORIOS_ANTICIPO_PROGRAMADOS_SCHEDULER_ZONE=America/Bogota
SGIE_RECORDATORIOS_ANTICIPO_PROGRAMADOS_SCHEDULER_LIMITE=50

SGIE_CALENDARIO_PRUEBA_PLATO_CHEF_CORREO=chef@club.com
SGIE_CALENDARIO_PRUEBA_PLATO_GERENTE_CORREO=gerente@club.com
SGIE_CALENDARIO_PRUEBA_PLATO_TESORERO_CORREO=tesorero@club.com
SGIE_CALENDARIO_EVENTO_CONFIRMADO_ASISTENTES_CORREOS=gerente@club.com,tesorero@club.com

SGIE_CALENDARIO_GOOGLE_ENABLED=false
SGIE_CALENDARIO_GOOGLE_CALENDAR_ID=primary
SGIE_CALENDARIO_GOOGLE_OAUTH_CLIENT_ID=
SGIE_CALENDARIO_GOOGLE_OAUTH_CLIENT_SECRET=
SGIE_CALENDARIO_GOOGLE_OAUTH_REFRESH_TOKEN=
SGIE_CALENDARIO_GOOGLE_APPLICATION_NAME=SGIE Club Boyaca
SGIE_CALENDARIO_GOOGLE_TIME_ZONE=America/Bogota
SGIE_CALENDARIO_GOOGLE_SEND_UPDATES=all
SGIE_CALENDARIO_GOOGLE_INCLUDE_ATTENDEES=true
```

En desarrollo pueden usarse valores locales. En produccion deben configurarse directamente en el servidor o plataforma de despliegue, sin guardar credenciales reales en Git.

Para enviar correos temporales desde el outbox de notificaciones, activar SMTP:

```env
SGIE_EMAIL_ENABLED=true
SGIE_EMAIL_FROM=cuenta-operativa@club.com
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=cuenta-operativa@club.com
MAIL_PASSWORD=APP_PASSWORD_O_PASSWORD_SMTP
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
```

Si `SGIE_EMAIL_ENABLED=false`, el sistema no envia correos reales y solo registra el intento en logs, igual que el adaptador temporal de WhatsApp.

Para usar Google Calendar se requiere OAuth. Este modo permite crear eventos con asistentes reales y enviar invitaciones por correo desde una cuenta operativa del Club.

1. En Google Cloud, habilitar Google Calendar API.
2. Crear credenciales OAuth Client ID de tipo Web application.
3. Agregar como Authorized redirect URI: `http://localhost:8085/oauth2callback` para desarrollo.
4. Obtener `client_id` y `client_secret`.
5. Generar la URL de autorizacion:

```powershell
.\scripts\google-calendar-oauth-url.ps1 -ClientId "TU_CLIENT_ID"
```

6. Abrir la URL, autorizar con la cuenta operativa del Club y copiar el parametro `code` de la URL final.
7. Intercambiar el codigo por tokens:

```powershell
.\scripts\google-calendar-oauth-token.ps1 -ClientId "TU_CLIENT_ID" -ClientSecret "TU_CLIENT_SECRET" -Code "CODIGO_RECIBIDO"
```

8. Configurar las variables:

```env
SGIE_CALENDARIO_GOOGLE_ENABLED=true
SGIE_CALENDARIO_GOOGLE_CALENDAR_ID=primary
SGIE_CALENDARIO_GOOGLE_OAUTH_CLIENT_ID=TU_CLIENT_ID
SGIE_CALENDARIO_GOOGLE_OAUTH_CLIENT_SECRET=TU_CLIENT_SECRET
SGIE_CALENDARIO_GOOGLE_OAUTH_REFRESH_TOKEN=TU_REFRESH_TOKEN
SGIE_CALENDARIO_GOOGLE_SEND_UPDATES=all
SGIE_CALENDARIO_GOOGLE_INCLUDE_ATTENDEES=true
```

## Endpoints de ejemplo

El contrato principal para frontend esta documentado en `docs/api-contract.md`.

Para ejecutar una prueba E2E local del flujo principal:

```powershell
.\scripts\e2e-flujo-principal.ps1
```

Para Railway:

```powershell
.\scripts\e2e-flujo-principal.ps1 -BaseUrl "https://sgie-cb-production.up.railway.app" -Nombre "Administrador" -Contrasena "admin123"
```

El script crea por API los catalogos necesarios para cliente, evento, reserva, menu, montaje, cotizacion, pago, prueba de plato, Calendar y email.

- `POST /api/clientes`
- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `POST /api/productos`
- `GET /api/productos`
- `GET /api/productos/{id}`

## Cómo crear un nuevo módulo

1. Crear el paquete raíz del módulo, por ejemplo `inventario`.
2. Crear sus capas: `presentacion`, `aplicacion`, `dominio`, `infraestructura`.
3. Definir entidades y puertos en `dominio`.
4. Definir casos de uso y servicios en `aplicacion`.
5. Implementar controladores en `presentacion`.
6. Implementar repositorios, adaptadores o integraciones en `infraestructura`.
