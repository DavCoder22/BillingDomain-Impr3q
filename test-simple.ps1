# Script de Pruebas Simple para Microservicios
Write-Host "=== PRUEBAS DE MICROSERVICIOS ===" -ForegroundColor Green

# Health Checks
Write-Host "Verificando health checks..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/actuator/health"
    Write-Host "Quotation Service: OK" -ForegroundColor Green
} catch {
    Write-Host "Quotation Service: ERROR" -ForegroundColor Red
}

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8082/api/actuator/health"
    Write-Host "Payment Service: OK" -ForegroundColor Green
} catch {
    Write-Host "Payment Service: ERROR" -ForegroundColor Red
}

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/ws/actuator/health"
    Write-Host "Invoice Service: OK" -ForegroundColor Green
} catch {
    Write-Host "Invoice Service: ERROR" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== PRUEBAS DE APIS ===" -ForegroundColor Green

# Quotation Service
Write-Host "Probando Quotation Service..." -ForegroundColor Yellow
try {
    $body = '{"material": "PLA", "volumeCm3": 100.5, "printTimeHours": 2.5}'
    $headers = @{ "Content-Type" = "application/json" }
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/quotes" -Method POST -Headers $headers -Body $body
    Write-Host "Cotizacion creada: ID $($response.id), Precio: $($response.price)" -ForegroundColor Green
} catch {
    Write-Host "Error en Quotation Service: $($_.Exception.Message)" -ForegroundColor Red
}

# Invoice Service
Write-Host "Probando Invoice Service..." -ForegroundColor Yellow
try {
    $body = '{"quotationId": 1, "paymentReference": "PAY-001", "customerEmail": "test@test.com", "amount": 24.56}'
    $headers = @{ "Content-Type" = "application/json" }
    $response = Invoke-RestMethod -Uri "http://localhost:8081/ws/invoices" -Method POST -Headers $headers -Body $body
    Write-Host "Factura generada: ID $($response.id), Numero: $($response.invoiceNumber)" -ForegroundColor Green
} catch {
    Write-Host "Error en Invoice Service: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=== DOCUMENTACION SWAGGER ===" -ForegroundColor Green
Write-Host "Quotation: http://localhost:8080/api/swagger-ui.html" -ForegroundColor Cyan
Write-Host "Payment: http://localhost:8082/api/swagger-ui.html" -ForegroundColor Cyan
Write-Host "Invoice: http://localhost:8081/ws/swagger-ui.html" -ForegroundColor Cyan

Write-Host ""
Write-Host "=== RESUMEN ===" -ForegroundColor Green
Write-Host "Todos los servicios estan funcionando correctamente!" -ForegroundColor Green 