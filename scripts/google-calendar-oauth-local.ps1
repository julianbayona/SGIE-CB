param(
    [Parameter(Mandatory = $true)]
    [string]$ClientId,

    [Parameter(Mandatory = $true)]
    [string]$ClientSecret,

    [string]$RedirectUri = "http://localhost:8085/oauth2callback",

    [string]$Scope = "https://www.googleapis.com/auth/calendar"
)

$listenerPrefix = "http://localhost:8085/"
$encodedClientId = [uri]::EscapeDataString($ClientId)
$encodedRedirectUri = [uri]::EscapeDataString($RedirectUri)
$encodedScope = [uri]::EscapeDataString($Scope)
$authUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id=$encodedClientId&redirect_uri=$encodedRedirectUri&response_type=code&scope=$encodedScope&access_type=offline&prompt=consent"

$listener = [System.Net.HttpListener]::new()
$listener.Prefixes.Add($listenerPrefix)

try {
    $listener.Start()
    Write-Host "Servidor local escuchando en $RedirectUri"
    Write-Host ""
    Write-Host "Abre esta URL, autoriza la cuenta operativa y espera el resultado en esta consola:"
    Write-Host $authUrl
    Write-Host ""

    $context = $listener.GetContext()
    $request = $context.Request
    $response = $context.Response

    if ($request.Url.AbsolutePath -ne "/oauth2callback") {
        $message = "Ruta invalida. Esperaba /oauth2callback."
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($message)
        $response.StatusCode = 404
        $response.OutputStream.Write($bytes, 0, $bytes.Length)
        $response.Close()
        throw $message
    }

    $errorParam = $request.QueryString["error"]
    if ($errorParam) {
        $message = "Google devolvio error: $errorParam"
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($message)
        $response.StatusCode = 400
        $response.OutputStream.Write($bytes, 0, $bytes.Length)
        $response.Close()
        throw $message
    }

    $code = $request.QueryString["code"]
    if ([string]::IsNullOrWhiteSpace($code)) {
        $message = "No llego el parametro code desde Google."
        $bytes = [System.Text.Encoding]::UTF8.GetBytes($message)
        $response.StatusCode = 400
        $response.OutputStream.Write($bytes, 0, $bytes.Length)
        $response.Close()
        throw $message
    }

    $successHtml = @"
<!doctype html>
<html lang="es">
<head><meta charset="utf-8"><title>SGIE OAuth</title></head>
<body style="font-family: sans-serif;">
  <h1>Autorizacion recibida</h1>
  <p>Puedes volver a la consola de PowerShell.</p>
</body>
</html>
"@
    $bytes = [System.Text.Encoding]::UTF8.GetBytes($successHtml)
    $response.ContentType = "text/html; charset=utf-8"
    $response.OutputStream.Write($bytes, 0, $bytes.Length)
    $response.Close()

    $body = @{
        client_id = $ClientId
        client_secret = $ClientSecret
        code = $code
        redirect_uri = $RedirectUri
        grant_type = "authorization_code"
    }

    $tokenResponse = Invoke-RestMethod `
        -Method Post `
        -Uri "https://oauth2.googleapis.com/token" `
        -ContentType "application/x-www-form-urlencoded" `
        -Body $body

    Write-Host ""
    Write-Host "Respuesta de Google:"
    $tokenResponse | ConvertTo-Json -Depth 5

    if ($tokenResponse.refresh_token) {
        Write-Host ""
        Write-Host "Configura esta variable en tu entorno de despliegue:"
        Write-Host "SGIE_CALENDARIO_GOOGLE_OAUTH_REFRESH_TOKEN=$($tokenResponse.refresh_token)"
    } else {
        Write-Host ""
        Write-Host "Google no devolvio refresh_token."
        Write-Host "Revoca el acceso anterior de la app en la cuenta Google y ejecuta de nuevo este script."
    }
} finally {
    if ($listener.IsListening) {
        $listener.Stop()
    }
    $listener.Close()
}
