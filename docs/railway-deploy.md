# Despliegue en Railway

Esta guia asume un servicio Railway para el backend Spring Boot y un plugin PostgreSQL asociado al proyecto.

## 1. Servicios

1. Crear un proyecto en Railway.
2. Agregar PostgreSQL.
3. Agregar el backend desde el repositorio GitHub.
4. Railway detectara el `Dockerfile` y construira la aplicacion.

## 2. Base de datos

Railway PostgreSQL expone variables como:

```env
PGHOST
PGPORT
PGDATABASE
PGUSER
PGPASSWORD
```

La aplicacion ya las lee automaticamente. Si existen, tienen prioridad sobre:

```env
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
```

## 3. Variables obligatorias

Configurar en Railway, nunca en Git:

```env
SGIE_AUTH_JWT_SECRET=usar-un-secreto-largo-y-unico
SGIE_AUTH_JWT_EXPIRATION_MINUTES=30
FLYWAY_ENABLED=true
HIBERNATE_DDL_AUTO=update
```

Railway asigna `PORT`; la aplicacion ya lo usa automaticamente.

## 4. Google Calendar

Configurar con los valores reales generados desde Google Cloud:

```env
SGIE_CALENDARIO_GOOGLE_ENABLED=true
SGIE_CALENDARIO_GOOGLE_CALENDAR_ID=correo-operativo@dominio.com
SGIE_CALENDARIO_GOOGLE_OAUTH_CLIENT_ID=...
SGIE_CALENDARIO_GOOGLE_OAUTH_CLIENT_SECRET=...
SGIE_CALENDARIO_GOOGLE_OAUTH_REFRESH_TOKEN=...
SGIE_CALENDARIO_GOOGLE_SEND_UPDATES=all
SGIE_CALENDARIO_GOOGLE_INCLUDE_ATTENDEES=true
```

Para produccion se recomienda usar una cuenta operativa del Club, no una cuenta personal.

## 5. Email SMTP

Para Gmail se requiere contrasena de aplicacion:

```env
SGIE_EMAIL_ENABLED=true
SGIE_EMAIL_FROM=correo-operativo@dominio.com
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=correo-operativo@dominio.com
MAIL_PASSWORD=app-password
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
```

## 6. Notificaciones internas

```env
SGIE_NOTIFICACIONES_PRUEBA_PLATO_CHEF_CORREO=chef@club.com
SGIE_NOTIFICACIONES_PRUEBA_PLATO_GERENTE_CORREO=gerente@club.com
SGIE_NOTIFICACIONES_PRUEBA_PLATO_TESORERO_CORREO=tesorero@club.com
SGIE_NOTIFICACIONES_PERSONAL_CORREOS=gerente@club.com,tesorero@club.com
SGIE_NOTIFICACIONES_PERSONAL_GRUPO_WHATSAPP_ID=grupo-personal@g.us
```

WhatsApp sigue usando adaptador de log temporal.

## 7. Schedulers

Valores recomendados iniciales:

```env
SGIE_CALENDARIO_SCHEDULER_FIXED_DELAY_MS=60000
SGIE_CALENDARIO_SCHEDULER_LIMITE=20
SGIE_NOTIFICACIONES_SCHEDULER_FIXED_DELAY_MS=60000
SGIE_NOTIFICACIONES_SCHEDULER_LIMITE=20
SGIE_RECORDATORIOS_ANTICIPO_PROGRAMADOS_SCHEDULER_ENABLED=true
SGIE_RECORDATORIOS_ANTICIPO_PROGRAMADOS_SCHEDULER_CRON=0 0 9 * * *
SGIE_RECORDATORIOS_ANTICIPO_PROGRAMADOS_SCHEDULER_ZONE=America/Bogota
```

## 8. Verificacion post-despliegue

Probar login:

```powershell
$body = @{
  nombre = "Administrador Prueba"
  contrasena = "admin123"
} | ConvertTo-Json

$res = Invoke-RestMethod `
  -Method Post `
  -Uri "https://TU-SERVICIO.up.railway.app/api/auth/login" `
  -ContentType "application/json" `
  -Body $body

$token = $res.accessToken
```

Probar endpoint protegido:

```powershell
Invoke-RestMethod `
  -Uri "https://TU-SERVICIO.up.railway.app/api/auth/me" `
  -Headers @{ Authorization = "Bearer $token" }
```

## 9. Seguridad

Si alguna credencial fue compartida por chat, capturas o repositorio, rotarla antes de produccion:

- Google OAuth client secret.
- Google OAuth refresh token.
- Gmail app password.
- Credenciales de PostgreSQL.
- `SGIE_AUTH_JWT_SECRET`.

## 10. Google OAuth Consent Screen

Cuando Railway entregue la URL publica del backend, usar estas paginas publicas en Google Cloud:

```text
Pagina principal:
https://TU-SERVICIO.up.railway.app/

Politica de privacidad:
https://TU-SERVICIO.up.railway.app/privacy

Terminos del servicio:
https://TU-SERVICIO.up.railway.app/terms
```

Si luego se usa dominio propio, reemplazar las URLs de Railway por el dominio final.
