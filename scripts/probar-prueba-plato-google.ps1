$ErrorActionPreference = "Stop"

$eventoId = "00000000-0000-0000-0000-000000000005"
$baseUrl = "http://localhost:8080/api"

$login = Invoke-RestMethod `
    -Method Post `
    -Uri "$baseUrl/auth/login" `
    -ContentType "application/json" `
    -Body (@{
        nombre = "Administrador Prueba"
        contrasena = "admin123"
    } | ConvertTo-Json)

$headers = @{ Authorization = "Bearer $($login.accessToken)" }

$body = @{
    fechaRealizacion = "2026-08-08T11:00:00"
} | ConvertTo-Json

Invoke-RestMethod `
    -Method Post `
    -Uri "$baseUrl/eventos/$eventoId/pruebas-plato" `
    -Headers $headers `
    -ContentType "application/json" `
    -Body $body
