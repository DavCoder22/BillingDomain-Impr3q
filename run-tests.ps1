# Script para ejecutar pruebas de todos los microservicios
# Windows PowerShell

# Configuración
$ErrorActionPreference = "Stop"

Write-Host "=== Iniciando pruebas de microservicios ===" -ForegroundColor Cyan

# Función para ejecutar Maven
function Invoke-MavenTest {
    param([string]$module)
    Write-Host "`nProbando $module..." -ForegroundColor Yellow
    try {
        & .\mvnw.cmd -q -pl $module test
        if ($LASTEXITCODE -ne 0) {
            throw "Error en pruebas de $module"
        }
        Write-Host "✓ $module completado" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Error en $module : $_" -ForegroundColor Red
        exit 1
    }
}

# Ejecutar pruebas en orden
$modules = @("quotation-service", "payment-service", "invoice-service")

foreach ($module in $modules) {
    Invoke-MavenTest -module $module
}

Write-Host "`nTodas las pruebas completadas exitosamente!" -ForegroundColor Green
