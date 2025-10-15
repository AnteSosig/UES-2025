# UES Gaming Docker Management Script

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet('start', 'stop', 'restart', 'logs', 'status', 'rebuild')]
    [string]$Action = 'start'
)

$ErrorActionPreference = "Stop"

Write-Host "UES Gaming Docker Management" -ForegroundColor Cyan
Write-Host "=============================" -ForegroundColor Cyan
Write-Host ""

switch ($Action) {
    'start' {
        Write-Host "Starting all services..." -ForegroundColor Green
        docker-compose up -d
        Write-Host ""
        Write-Host "Services started successfully!" -ForegroundColor Green
        Write-Host ""
        Write-Host "Access points:" -ForegroundColor Yellow
        Write-Host "  - Backend API:      http://localhost:8080" -ForegroundColor White
        Write-Host "  - Elasticsearch:    http://localhost:9200" -ForegroundColor White
        Write-Host "  - MinIO Console:    http://localhost:9001" -ForegroundColor White
        Write-Host "  - MySQL:            localhost:3306" -ForegroundColor White
        Write-Host ""
        Write-Host "MinIO Credentials: minioadmin / minioadmin" -ForegroundColor Cyan
    }
    'stop' {
        Write-Host "Stopping all services..." -ForegroundColor Yellow
        docker-compose down
        Write-Host "Services stopped successfully!" -ForegroundColor Green
    }
    'restart' {
        Write-Host "Restarting all services..." -ForegroundColor Yellow
        docker-compose restart
        Write-Host "Services restarted successfully!" -ForegroundColor Green
    }
    'logs' {
        Write-Host "Showing logs (Ctrl+C to exit)..." -ForegroundColor Yellow
        docker-compose logs -f
    }
    'status' {
        Write-Host "Service Status:" -ForegroundColor Yellow
        docker-compose ps
    }
    'rebuild' {
        Write-Host "Rebuilding backend service..." -ForegroundColor Yellow
        docker-compose down
        docker-compose build backend
        docker-compose up -d
        Write-Host "Backend rebuilt and started successfully!" -ForegroundColor Green
    }
}

Write-Host ""
