param(
    [Parameter(Mandatory = $true)]
    [string]$ClientId,

    [string]$RedirectUri = "http://localhost:8085/oauth2callback"
)

$scope = [uri]::EscapeDataString("https://www.googleapis.com/auth/calendar")
$redirect = [uri]::EscapeDataString($RedirectUri)
$client = [uri]::EscapeDataString($ClientId)

$url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=$client&redirect_uri=$redirect&response_type=code&scope=$scope&access_type=offline&prompt=consent"

Write-Host "Abre esta URL en el navegador, autoriza la cuenta operativa y copia el parametro code de la URL final:"
Write-Host $url
