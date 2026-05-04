param(
    [Parameter(Mandatory = $true)]
    [string]$ClientId,

    [Parameter(Mandatory = $true)]
    [string]$ClientSecret,

    [Parameter(Mandatory = $true)]
    [string]$Code,

    [string]$RedirectUri = "http://localhost:8085/oauth2callback"
)

$body = @{
    client_id = $ClientId
    client_secret = $ClientSecret
    code = $Code
    redirect_uri = $RedirectUri
    grant_type = "authorization_code"
}

$response = Invoke-RestMethod `
    -Method Post `
    -Uri "https://oauth2.googleapis.com/token" `
    -ContentType "application/x-www-form-urlencoded" `
    -Body $body

$response | ConvertTo-Json -Depth 5

if ($response.refresh_token) {
    Write-Host ""
    Write-Host "Configura esta variable:"
    Write-Host "SGIE_CALENDARIO_GOOGLE_OAUTH_REFRESH_TOKEN=$($response.refresh_token)"
} else {
    Write-Host ""
    Write-Host "Google no devolvio refresh_token. Ejecuta de nuevo el flujo con prompt=consent o revoca el acceso previo de la app."
}
