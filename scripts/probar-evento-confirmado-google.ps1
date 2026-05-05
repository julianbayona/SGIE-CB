$ErrorActionPreference = "Stop"

$eventoId = "10000000-0000-0000-0000-000000000006"
$reservaRaizId = "10000000-0000-0000-0000-000000000007"
$baseUrl = "http://localhost:8080/api"

$login = Invoke-RestMethod `
    -Method Post `
    -Uri "$baseUrl/auth/login" `
    -ContentType "application/json" `
    -Body (@{
        nombre = "Administrador Confirmacion"
        contrasena = "admin123"
    } | ConvertTo-Json)

$headers = @{ Authorization = "Bearer $($login.accessToken)" }

try {
    $cotizacion = Invoke-RestMethod `
        -Method Get `
        -Uri "$baseUrl/reservas/$reservaRaizId/cotizacion-vigente" `
        -Headers $headers

    Write-Host "Usando cotizacion vigente existente: $($cotizacion.id), estado=$($cotizacion.estado)"
} catch {
    $cotizacion = Invoke-RestMethod `
        -Method Post `
        -Uri "$baseUrl/reservas/$reservaRaizId/cotizaciones" `
        -Headers $headers `
        -ContentType "application/json" `
        -Body (@{
            descuento = 0
            observaciones = "Cotizacion de prueba para confirmacion"
        } | ConvertTo-Json)

    Write-Host "Cotizacion creada: $($cotizacion.id)"
}

if ($cotizacion.estado -eq "BORRADOR") {
    $cotizacion = Invoke-RestMethod `
        -Method Patch `
        -Uri "$baseUrl/cotizaciones/$($cotizacion.id)/generar" `
        -Headers $headers

    Write-Host "Cotizacion generada: $($cotizacion.estado)"
}

if ($cotizacion.estado -eq "GENERADA" -or $cotizacion.estado -eq "ENVIADA") {
    $cotizacion = Invoke-RestMethod `
        -Method Patch `
        -Uri "$baseUrl/cotizaciones/$($cotizacion.id)/aceptar" `
        -Headers $headers

    Write-Host "Cotizacion aceptada: $($cotizacion.estado)"
}

$confirmado = Invoke-RestMethod `
    -Method Post `
    -Uri "$baseUrl/eventos/$eventoId/confirmar" `
    -Headers $headers

Write-Host "Evento confirmado: $($confirmado.estado)"
Write-Host "EventoId: $($confirmado.id)"
