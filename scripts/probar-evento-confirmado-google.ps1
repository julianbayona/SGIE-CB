$ErrorActionPreference = "Stop"

$usuarioId = "10000000-0000-0000-0000-000000000001"
$eventoId = "10000000-0000-0000-0000-000000000006"
$reservaRaizId = "10000000-0000-0000-0000-000000000007"
$baseUrl = "http://localhost:8080/api"

try {
    $cotizacion = Invoke-RestMethod `
        -Method Get `
        -Uri "$baseUrl/reservas/$reservaRaizId/cotizacion-vigente"

    Write-Host "Usando cotizacion vigente existente: $($cotizacion.id), estado=$($cotizacion.estado)"
} catch {
    $cotizacion = Invoke-RestMethod `
        -Method Post `
        -Uri "$baseUrl/reservas/$reservaRaizId/cotizaciones" `
        -ContentType "application/json" `
        -Body (@{
            usuarioId = $usuarioId
            descuento = 0
            observaciones = "Cotizacion de prueba para confirmacion"
        } | ConvertTo-Json)

    Write-Host "Cotizacion creada: $($cotizacion.id)"
}

if ($cotizacion.estado -eq "BORRADOR") {
    $cotizacion = Invoke-RestMethod `
        -Method Patch `
        -Uri "$baseUrl/cotizaciones/$($cotizacion.id)/generar"

    Write-Host "Cotizacion generada: $($cotizacion.estado)"
}

if ($cotizacion.estado -eq "GENERADA" -or $cotizacion.estado -eq "ENVIADA") {
    $cotizacion = Invoke-RestMethod `
        -Method Patch `
        -Uri "$baseUrl/cotizaciones/$($cotizacion.id)/aceptar"

    Write-Host "Cotizacion aceptada: $($cotizacion.estado)"
}

$confirmado = Invoke-RestMethod `
    -Method Post `
    -Uri "$baseUrl/eventos/$eventoId/confirmar" `
    -ContentType "application/json" `
    -Body (@{ usuarioId = $usuarioId } | ConvertTo-Json)

Write-Host "Evento confirmado: $($confirmado.estado)"
Write-Host "EventoId: $($confirmado.id)"
