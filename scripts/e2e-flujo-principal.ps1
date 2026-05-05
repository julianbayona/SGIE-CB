param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Nombre = "Administrador",
    [string]$Contrasena = "admin123",
    [string]$TipoMomentoId = "20000000-0000-0000-0000-000000000001",
    [string]$PlatoId = "20000000-0000-0000-0000-000000000002",
    [switch]$SeedMenuBasicoEnDocker,
    [string]$DbContainer = "sgie-postgres",
    [string]$DbUser = "postgres",
    [string]$DbName = "sgie"
)

$ErrorActionPreference = "Stop"

if (-not ($BaseUrl.StartsWith("http://") -or $BaseUrl.StartsWith("https://"))) {
    $BaseUrl = "https://$BaseUrl"
}
$BaseUrl = $BaseUrl.TrimEnd("/")

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Convert-Body {
    param($Body)
    if ($null -eq $Body) {
        return $null
    }
    return ($Body | ConvertTo-Json -Depth 12)
}

function Read-ErrorBody {
    param($Exception)
    try {
        $reader = New-Object System.IO.StreamReader($Exception.Response.GetResponseStream())
        return $reader.ReadToEnd()
    } catch {
        return $Exception.Message
    }
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        $Body = $null,
        $Headers = @{}
    )

    $uri = if ($Path.StartsWith("http")) { $Path } else { "$BaseUrl$Path" }
    $params = @{
        Method = $Method
        Uri = $uri
        Headers = $Headers
        ErrorAction = "Stop"
    }

    if ($null -ne $Body) {
        $params.ContentType = "application/json"
        $params.Body = Convert-Body $Body
    }

    try {
        return Invoke-RestMethod @params
    } catch {
        $bodyText = Read-ErrorBody $_.Exception
        throw "Fallo $Method $uri`n$bodyText"
    }
}

function Invoke-ApiDownload {
    param(
        [string]$Path,
        [string]$OutFile,
        $Headers = @{}
    )

    $uri = "$BaseUrl$Path"
    try {
        Invoke-WebRequest -Method Get -Uri $uri -Headers $Headers -OutFile $OutFile -ErrorAction Stop | Out-Null
    } catch {
        $bodyText = Read-ErrorBody $_.Exception
        throw "Fallo GET $uri`n$bodyText"
    }
}

if ($SeedMenuBasicoEnDocker) {
    Write-Step "Cargando seed minimo de menu en Docker"
    Get-Content -Raw "$PSScriptRoot\seed-menu-basico.sql" |
        docker exec -i $DbContainer psql -U $DbUser -d $DbName
}

$suffix = Get-Date -Format "yyyyMMddHHmmss"
$fechaEventoInicio = "2026-08-10T18:00:00"
$fechaEventoFin = "2026-08-11T02:00:00"
$fechaPruebaPlato = "2026-08-05T11:00:00"

Write-Step "Login"
$login = Invoke-Api -Method Post -Path "/api/auth/login" -Body @{
    nombre = $Nombre
    contrasena = $Contrasena
}
$headers = @{ Authorization = "Bearer $($login.accessToken)" }
Write-Host "Usuario autenticado: $($login.nombre) [$($login.rol)]"

Write-Step "Validando token"
$me = Invoke-Api -Method Get -Path "/api/auth/me" -Headers $headers
Write-Host "Sesion activa para usuarioId=$($me.usuarioId)"

Write-Step "Creando catalogos base"
$tipoEvento = Invoke-Api -Method Post -Path "/api/catalogos/tipos-evento" -Headers $headers -Body @{
    nombre = "Boda E2E $suffix"
    descripcion = "Tipo de evento para prueba E2E"
}
$tipoComida = Invoke-Api -Method Post -Path "/api/catalogos/tipos-comida" -Headers $headers -Body @{
    nombre = "Cena E2E $suffix"
    descripcion = "Tipo de comida para prueba E2E"
}
$color = Invoke-Api -Method Post -Path "/api/catalogos/colores" -Headers $headers -Body @{
    nombre = "Blanco E2E $suffix"
    codigoHex = "#FFFFFF"
}
$mantel = Invoke-Api -Method Post -Path "/api/catalogos/manteles" -Headers $headers -Body @{
    nombre = "Mantel blanco E2E $suffix"
    colorId = $color.id
}
$tipoMesa = Invoke-Api -Method Post -Path "/api/catalogos/tipos-mesa" -Headers $headers -Body @{
    nombre = "Mesa redonda E2E $suffix"
    descripcion = "Mesa para ocho personas"
}
$tipoSilla = Invoke-Api -Method Post -Path "/api/catalogos/tipos-silla" -Headers $headers -Body @{
    nombre = "Silla Tiffany E2E $suffix"
    descripcion = "Silla para montaje formal"
}
$tipoAdicional = Invoke-Api -Method Post -Path "/api/catalogos/tipos-adicional" -Headers $headers -Body @{
    nombre = "Sonido E2E $suffix"
    modoCobro = "SERVICIO"
    precioBase = 180000
}
$salon = Invoke-Api -Method Post -Path "/api/salones" -Headers $headers -Body @{
    nombre = "Salon E2E $suffix"
    capacidad = 120
    descripcion = "Salon para prueba E2E"
}
Write-Host "Catalogos creados"

Write-Step "Creando cliente"
$cliente = Invoke-Api -Method Post -Path "/api/clientes" -Headers $headers -Body @{
    cedula = "9$suffix"
    nombreCompleto = "Cliente E2E $suffix"
    telefono = "573001112233"
    correo = "julianbayona0315@gmail.com"
    tipoCliente = "NO_SOCIO"
}
Write-Host "ClienteId=$($cliente.id)"

Write-Step "Creando evento"
$evento = Invoke-Api -Method Post -Path "/api/eventos" -Headers $headers -Body @{
    clienteId = $cliente.id
    tipoEventoId = $tipoEvento.id
    tipoComidaId = $tipoComida.id
    fechaHoraInicio = $fechaEventoInicio
    fechaHoraFin = $fechaEventoFin
}
Write-Host "EventoId=$($evento.id), estado=$($evento.estado)"

Write-Step "Creando reserva"
$reserva = Invoke-Api -Method Post -Path "/api/eventos/$($evento.id)/reservas" -Headers $headers -Body @{
    salonId = $salon.id
    numInvitados = 80
    fechaHoraInicio = $fechaEventoInicio
    fechaHoraFin = $fechaEventoFin
}
Write-Host "ReservaId=$($reserva.id), reservaRaizId=$($reserva.reservaRaizId)"

Write-Step "Configurando menu"
try {
    $menu = Invoke-Api -Method Put -Path "/api/reservas/$($reserva.reservaRaizId)/menu" -Headers $headers -Body @{
        notasGenerales = "Menu E2E"
        selecciones = @(
            @{
                tipoMomentoId = $TipoMomentoId
                items = @(
                    @{
                        platoId = $PlatoId
                        cantidad = 80
                        excepciones = "2 vegetarianos"
                    }
                )
            }
        )
    }
    Write-Host "MenuId=$($menu.id)"
} catch {
    throw "No se pudo configurar menu. Si estas en local, ejecuta este script con -SeedMenuBasicoEnDocker o carga scripts/seed-menu-basico.sql en tu base de datos.`n$($_.Exception.Message)"
}

Write-Step "Configurando montaje"
$montaje = Invoke-Api -Method Put -Path "/api/reservas/$($reserva.reservaRaizId)/montaje" -Headers $headers -Body @{
    observaciones = "Montaje E2E"
    mesas = @(
        @{
            tipoMesaId = $tipoMesa.id
            tipoSillaId = $tipoSilla.id
            sillaPorMesa = 8
            cantidadMesas = 10
            mantelId = $mantel.id
            sobremantelId = $null
            vajilla = $true
            fajon = $false
        }
    )
    infraestructura = @{
        mesaPonque = $true
        mesaRegalos = $true
        espacioMusicos = $false
        estanteBombas = $false
    }
    adicionales = @(
        @{
            tipoAdicionalId = $tipoAdicional.id
            cantidad = 1
        }
    )
}
Write-Host "MontajeId=$($montaje.id)"

Write-Step "Creando y generando cotizacion"
$cotizacion = Invoke-Api -Method Post -Path "/api/reservas/$($reserva.reservaRaizId)/cotizaciones" -Headers $headers -Body @{
    descuento = 0
    observaciones = "Cotizacion E2E"
}
Write-Host "CotizacionId=$($cotizacion.id), items=$($cotizacion.items.Count), total=$($cotizacion.valorTotal)"

$cotizacion = Invoke-Api -Method Patch -Path "/api/cotizaciones/$($cotizacion.id)/generar" -Headers $headers
Write-Host "Estado cotizacion: $($cotizacion.estado)"

Write-Step "Descargando documento PDF"
$pdfPath = Join-Path $PSScriptRoot "cotizacion-e2e-$suffix.pdf"
Invoke-ApiDownload -Path "/api/cotizaciones/$($cotizacion.id)/documento" -OutFile $pdfPath -Headers $headers
Write-Host "PDF generado: $pdfPath"

Write-Step "Enviando cotizacion por email"
$cotizacionEmail = Invoke-Api -Method Post -Path "/api/cotizaciones/$($cotizacion.id)/enviar-email" -Headers $headers
Write-Host "Solicitud de email registrada. Estado cotizacion=$($cotizacionEmail.estado)"

Write-Step "Aceptando cotizacion"
$cotizacion = Invoke-Api -Method Patch -Path "/api/cotizaciones/$($cotizacion.id)/aceptar" -Headers $headers
Write-Host "Estado cotizacion: $($cotizacion.estado)"

Write-Step "Registrando anticipo"
$anticipo = Invoke-Api -Method Post -Path "/api/cotizaciones/$($cotizacion.id)/anticipos" -Headers $headers -Body @{
    valor = 300000
    metodoPago = "TRANSFERENCIA"
    fechaPago = (Get-Date).ToString("yyyy-MM-dd")
    observaciones = "Anticipo E2E"
}
Write-Host "AnticipoId=$($anticipo.id), valor=$($anticipo.valor)"

Write-Step "Consultando saldo del evento"
$estadoFinanciero = Invoke-Api -Method Get -Path "/api/eventos/$($evento.id)/estado-financiero" -Headers $headers
Write-Host "Total=$($estadoFinanciero.valorTotal), anticipos=$($estadoFinanciero.totalAnticipos), saldo=$($estadoFinanciero.saldoPendiente)"

Write-Step "Confirmando evento"
$eventoConfirmado = Invoke-Api -Method Post -Path "/api/eventos/$($evento.id)/confirmar" -Headers $headers
Write-Host "Estado evento: $($eventoConfirmado.estado)"

Write-Step "Creando prueba de plato"
$prueba = Invoke-Api -Method Post -Path "/api/eventos/$($evento.id)/pruebas-plato" -Headers $headers -Body @{
    fechaRealizacion = $fechaPruebaPlato
}
Write-Host "PruebaPlatoId=$($prueba.id), estado=$($prueba.estado)"

Write-Step "Resumen"
[pscustomobject]@{
    baseUrl = $BaseUrl
    clienteId = $cliente.id
    eventoId = $evento.id
    reservaId = $reserva.id
    reservaRaizId = $reserva.reservaRaizId
    menuId = $menu.id
    montajeId = $montaje.id
    cotizacionId = $cotizacion.id
    anticipoId = $anticipo.id
    pruebaPlatoId = $prueba.id
    pdf = $pdfPath
} | Format-List
