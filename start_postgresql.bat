@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo PostgreSQL Quick Start
echo ========================================
echo.

REM Поиск всех установленных служб PostgreSQL
set "service_count=0"
set "services_file=%TEMP%\postgresql_services_%RANDOM%.txt"
del "%services_file%" 2>nul

echo Searching for PostgreSQL services...
echo.

REM Пробуем стандартные имена служб PostgreSQL
set "common_services=postgresql-x64-15 postgresql-x64-16 postgresql-x64-17 postgresql-x64-14 postgresql-x64-13 postgresql-x64-12 postgresql-x64-11 postgresql-x64-10"

for %%s in (%common_services%) do (
    sc query "%%s" >nul 2>&1
    if !errorlevel!==0 (
        set /a service_count+=1
        echo !service_count!. Found: %%s
        echo %%s>>"%services_file%"
    )
)

REM Альтернативный поиск через sc query
if %service_count%==0 (
    echo Searching in all services...
    for /f "tokens=2 delims=: " %%a in ('sc query type^= service state^= all ^| findstr /i /c:"postgresql"') do (
        set "service_name=%%a"
        set "service_name=!service_name: =!"
        if not "!service_name!"=="" (
            sc query "!service_name!" >nul 2>&1
            if !errorlevel!==0 (
                set /a service_count+=1
                echo !service_count!. Found: !service_name!
                echo !service_name!>>"%services_file%"
            )
        )
    )
)

if %service_count%==0 (
    echo.
    echo [FAIL] No PostgreSQL services found!
    echo.
    echo Please install PostgreSQL or check if it's installed with a different name.
    echo.
    echo To check all services manually:
    echo   sc query type^= service state^= all ^| findstr /i postgresql
    echo.
    pause
    exit /b 1
)

echo.
echo Found %service_count% PostgreSQL service(s)
echo.

REM Если только одна служба, запускаем её автоматически
if %service_count%==1 (
    for /f "usebackq delims=" %%s in ("%services_file%") do (
        set "selected_service=%%s"
        goto :start_service
    )
) else (
    REM Если несколько служб, показываем меню выбора
    echo Select PostgreSQL service to start:
    echo.
    set "line_num=0"
    for /f "usebackq delims=" %%s in ("%services_file%") do (
        set /a line_num+=1
        echo !line_num!. %%s
    )
    echo.
    set /p "service_choice=Enter service number (1-%service_count%): "
    
    set "line_num=0"
    for /f "usebackq delims=" %%s in ("%services_file%") do (
        set /a line_num+=1
        if !line_num!==!service_choice! (
            set "selected_service=%%s"
            goto :start_service
        )
    )
    echo Invalid selection.
    pause
    exit /b 1
)

:start_service
echo.
echo Starting PostgreSQL service: %selected_service%
echo.

REM Проверяем статус службы перед запуском
sc query "%selected_service%" | findstr /i "RUNNING" >nul
if !errorlevel!==0 (
    echo [OK] PostgreSQL service is already running!
    echo Service: %selected_service%
    echo.
    pause
    exit /b 0
)

REM Запускаем службу
net start "%selected_service%"
if %errorlevel%==0 (
    echo.
    echo [OK] PostgreSQL service started successfully!
    echo.
    echo Service: %selected_service%
    echo Status: Running
    echo.
    echo You can now run the Coffee Shop application.
) else (
    echo.
    echo [FAIL] Failed to start PostgreSQL service.
    echo Error code: %errorlevel%
    echo.
    echo Possible reasons:
    echo - Insufficient permissions (run this script as Administrator)
    echo - Service is disabled
    echo - Service dependencies are not met
    echo.
    echo To run as Administrator:
    echo   1. Right-click on this file
    echo   2. Select "Run as administrator"
    echo.
    pause
    exit /b 1
)

REM Очистка временных файлов
del "%services_file%" 2>nul

echo.
pause
exit /b 0







