@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM Поиск PostgreSQL bin directory и добавление в PATH
set "PG_BIN="
for /d %%d in ("C:\Program Files\PostgreSQL\*") do (
    if exist "%%d\bin\psql.exe" (
        set "PG_BIN=%%d\bin"
        goto :found_pg
    )
)
for /d %%d in ("C:\Program Files (x86)\PostgreSQL\*") do (
    if exist "%%d\bin\psql.exe" (
        set "PG_BIN=%%d\bin"
        goto :found_pg
    )
)
:found_pg
if not "!PG_BIN!"=="" (
    set "PATH=!PG_BIN!;%PATH%"
)

echo ========================================
echo Complete PostgreSQL Setup
echo ========================================
echo This script will:
echo   1. Start PostgreSQL service
echo   2. Create database 'coffeeshop_db'
echo   3. Verify setup
echo ========================================
echo.

REM Шаг 1: Запуск PostgreSQL
echo [Step 1/3] Starting PostgreSQL service...
call start_postgresql.bat
if %errorlevel% neq 0 (
    echo.
    echo [FAIL] Failed to start PostgreSQL. Please start it manually.
    pause
    exit /b 1
)

echo.
echo Waiting for PostgreSQL to be ready...
ping 127.0.0.1 -n 4 >nul

REM Шаг 2: Создание базы данных
echo.
echo [Step 2/3] Creating database 'coffeeshop_db'...
call create_database.bat
if %errorlevel% neq 0 (
    echo.
    echo [FAIL] Failed to create database. Please create it manually.
    pause
    exit /b 1
)

REM Шаг 3: Проверка
echo.
echo [Step 3/3] Verifying setup...
echo.

REM Проверяем подключение к созданной БД
if not "!PG_BIN!"=="" (
    "!PG_BIN!\psql.exe" -U postgres -d coffeeshop_db -c "SELECT version();" >nul 2>&1
    if %errorlevel% neq 0 (
        "!PG_BIN!\psql.exe" -U postgres -h localhost -d coffeeshop_db -c "SELECT version();" >nul 2>&1
    )
) else (
    psql -U postgres -d coffeeshop_db -c "SELECT version();" >nul 2>&1
    if %errorlevel% neq 0 (
        psql -U postgres -h localhost -d coffeeshop_db -c "SELECT version();" >nul 2>&1
    )
)
if %errorlevel%==0 (
    echo [OK] Setup completed successfully!
    echo.
    echo PostgreSQL is running
    echo Database 'coffeeshop_db' is created and accessible
    echo.
    echo You can now run the Coffee Shop application.
) else (
    echo [WARN] Setup completed, but verification failed.
    echo Please check database manually.
)

echo.
pause
exit /b 0







