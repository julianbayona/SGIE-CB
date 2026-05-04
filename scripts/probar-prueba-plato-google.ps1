$ErrorActionPreference = "Stop"

$eventoId = "00000000-0000-0000-0000-000000000005"
$usuarioId = "00000000-0000-0000-0000-000000000001"
$url = "http://localhost:8080/api/eventos/$eventoId/pruebas-plato"

$body = @{
    usuarioId = $usuarioId
    fechaRealizacion = "2026-05-05T10:00:00"
} | ConvertTo-Json

Invoke-RestMethod `
    -Method Post `
    -Uri $url `
    -ContentType "application/json" `
    -Body $body
