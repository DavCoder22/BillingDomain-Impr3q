# Script para Renovar Tokens de AWS Educate
# Autor: Sistema de Billing DM
# Fecha: 2025-07-06

param(
    [string]$GitHubToken,
    [string]$RepoOwner,
    [string]$RepoName
)

Write-Host "🔄 Script de Renovación de Tokens AWS Educate" -ForegroundColor Cyan
Write-Host ""

# Verificar parámetros
if (-not $GitHubToken) {
    Write-Host "❌ Error: Debes proporcionar un GitHub Token" -ForegroundColor Red
    Write-Host "Uso: .\renew-aws-tokens.ps1 -GitHubToken 'tu_token' -RepoOwner 'tu_usuario' -RepoName 'tu_repo'" -ForegroundColor Yellow
    exit 1
}

Write-Host "📋 Pasos para renovar tokens:" -ForegroundColor Yellow
Write-Host "1. Ve a AWS Educate → Tu cuenta → AWS Account" -ForegroundColor White
Write-Host "2. Haz clic en 'AWS Console'" -ForegroundColor White
Write-Host "3. Ve a IAM → Users → Tu usuario → Security credentials" -ForegroundColor White
Write-Host "4. Create access key (temporal)" -ForegroundColor White
Write-Host "5. Copia los valores que aparecen a continuación" -ForegroundColor White
Write-Host ""

# Solicitar credenciales
$accessKey = Read-Host "🔑 AWS Access Key ID"
$secretKey = Read-Host "🔐 AWS Secret Access Key"
$sessionToken = Read-Host "🎫 AWS Session Token"

if (-not $accessKey -or -not $secretKey -or -not $sessionToken) {
    Write-Host "❌ Error: Todos los campos son requeridos" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🔄 Actualizando secrets en GitHub..." -ForegroundColor Yellow

# Función para actualizar secret
function Update-GitHubSecret {
    param(
        [string]$SecretName,
        [string]$SecretValue
    )
    
    $headers = @{
        "Authorization" = "token $GitHubToken"
        "Accept" = "application/vnd.github.v3+json"
    }
    
    $body = @{
        encrypted_value = $SecretValue
        key_id = "your-key-id" # Esto se obtiene de la API de GitHub
    } | ConvertTo-Json
    
    try {
        $url = "https://api.github.com/repos/$RepoOwner/$RepoName/actions/secrets/$SecretName"
        $response = Invoke-RestMethod -Uri $url -Method PUT -Headers $headers -Body $body
        Write-Host "✅ $SecretName actualizado" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Error actualizando $SecretName : $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Actualizar secrets
Update-GitHubSecret -SecretName "AWS_ACCESS_KEY_ID" -SecretValue $accessKey
Update-GitHubSecret -SecretName "AWS_SECRET_ACCESS_KEY" -SecretValue $secretKey
Update-GitHubSecret -SecretName "AWS_SESSION_TOKEN" -SecretValue $sessionToken

Write-Host ""
Write-Host "✅ Proceso completado!" -ForegroundColor Green
Write-Host "🕐 Tokens renovados en: $(Get-Date)" -ForegroundColor Cyan
Write-Host "⏰ Recuerda renovar en 12 horas" -ForegroundColor Yellow
Write-Host ""

# Crear recordatorio
$reminder = @"
# Recordatorio de Renovación de Tokens AWS Educate

## Última renovación: $(Get-Date)
## Próxima renovación: $((Get-Date).AddHours(12))

### Tokens actualizados:
- AWS_ACCESS_KEY_ID: $($accessKey.Substring(0,4))...
- AWS_SECRET_ACCESS_KEY: $($secretKey.Substring(0,4))...
- AWS_SESSION_TOKEN: $($sessionToken.Substring(0,4))...

### Pasos para renovar:
1. Ve a AWS Educate → Tu cuenta → AWS Account
2. Haz clic en 'AWS Console'
3. Ve a IAM → Users → Tu usuario → Security credentials
4. Create access key (temporal)
5. Ejecuta este script nuevamente

### Comando para ejecutar:
```powershell
.\renew-aws-tokens.ps1 -GitHubToken 'tu_token' -RepoOwner 'tu_usuario' -RepoName 'tu_repo'
```
"@

$reminder | Out-File -FilePath "aws-token-reminder.md" -Encoding UTF8
Write-Host "📝 Recordatorio guardado en: aws-token-reminder.md" -ForegroundColor Cyan 