@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo PostgreSQL Docker Setup
echo ========================================
echo.

REM Проверка Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Docker is not installed or not in PATH
    echo.
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo [OK] Docker found
echo.

REM Проверка, запущен ли Docker daemon
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo [FAIL] Docker daemon is not running
    echo.
    echo Please start Docker Desktop and wait until it's ready.
    echo Then run this script again.
    pause
    exit /b 1
)

echo [OK] Docker daemon is running
echo.

REM Переход в директорию с docker-compose.yml
cd /d "%~dp0src"

echo Starting PostgreSQL container...
echo.

docker-compose up -d

if %errorlevel%==0 (
    echo.
    echo [OK] PostgreSQL container started successfully!
    echo.
    echo Waiting for PostgreSQL to be ready...
    timeout /t 5 /nobreak >nul
    
    echo.
    echo Container details:
    docker ps | findstr coffeeshop_db_container
    echo.
    echo Database connection info:
    echo   Host: localhost
    echo   Port: 5432
    echo   Database: coffeeshop_db
    echo   User: sa
    echo   Password: admin
    echo.
    echo You can now run the Coffee Shop application.
) else (
    echo.
    echo [FAIL] Failed to start PostgreSQL container
    echo.
    echo Check Docker logs:
    echo   docker-compose logs
    echo.
    echo Or check if port 5432 is already in use:
    echo   netstat -ano | findstr ":5432"
)

echo.
pause
exit /b 0





