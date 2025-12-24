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
echo PostgreSQL Database Creator
echo ========================================
echo.

REM Проверка наличия psql
if not "!PG_BIN!"=="" (
    set "PSQL_CMD=!PG_BIN!\psql.exe"
    echo [OK] psql found at: !PG_BIN!
) else (
    where psql >nul 2>&1
    if %errorlevel% neq 0 (
        echo [FAIL] psql command not found!
        echo.
        echo Please ensure PostgreSQL is installed and added to PATH.
        echo.
        echo To add PostgreSQL to PATH:
        echo   1. Find PostgreSQL installation directory (usually C:\Program Files\PostgreSQL\XX\bin)
        echo   2. Add it to System Environment Variables PATH
        echo   3. Restart command prompt
        echo.
        pause
        exit /b 1
    )
    set "PSQL_CMD=psql"
    echo [OK] psql found
)
echo.

REM Запрашиваем параметры подключения
set /p "db_user=Enter PostgreSQL username [postgres]: "
if "!db_user!"=="" set "db_user=postgres"

set /p "db_host=Enter PostgreSQL host [localhost]: "
if "!db_host!"=="" set "db_host=localhost"

set /p "db_port=Enter PostgreSQL port [5432]: "
if "!db_port!"=="" set "db_port=5432"

set /p "db_password=Enter PostgreSQL password (press Enter to skip if using trust auth): "

set "db_name=coffeeshop_db"

echo.
echo Configuration:
echo   Username: %db_user%
echo   Host: %db_host%
echo   Port: %db_port%
echo   Database: %db_name%
echo.

REM Проверяем, существует ли база данных
echo Checking if database '%db_name%' already exists...
echo.

REM Пробуем подключиться через локальный сокет сначала (без -h)
if not "!db_password!"=="" (
    set "PGPASSWORD=!db_password!"
)
%PSQL_CMD% -U %db_user% -p %db_port% -lqt 2>nul | findstr /i "%db_name%" >nul
if %errorlevel% neq 0 (
    REM Если не получилось, пробуем через TCP/IP
    if not "!db_password!"=="" (
        set "PGPASSWORD=!db_password!"
    )
    %PSQL_CMD% -U %db_user% -h %db_host% -p %db_port% -lqt 2>nul | findstr /i "%db_name%" >nul
)
if %errorlevel%==0 (
    echo [WARN] Database '%db_name%' already exists!
    echo.
    set /p "overwrite=Do you want to drop and recreate it? (y/n): "
    if /i not "!overwrite!"=="y" (
        echo Operation cancelled.
        pause
        exit /b 0
    )
    
    echo.
    echo Dropping existing database...
    if not "!db_password!"=="" (
        set "PGPASSWORD=!db_password!"
    )
    %PSQL_CMD% -U %db_user% -p %db_port% -c "DROP DATABASE IF EXISTS %db_name%;" 2>nul
    if %errorlevel% neq 0 (
        if not "!db_password!"=="" (
            set "PGPASSWORD=!db_password!"
        )
        %PSQL_CMD% -U %db_user% -h %db_host% -p %db_port% -c "DROP DATABASE IF EXISTS %db_name%;" 2>nul
    )
    if %errorlevel% neq 0 (
        echo [FAIL] Failed to drop database. You may need to disconnect all sessions first.
        echo.
        echo To manually drop the database:
        echo   psql -U %db_user% -p %db_port%
        echo   DROP DATABASE %db_name%;
        pause
        exit /b 1
    )
    echo [OK] Database dropped successfully
    echo.
)

REM Создаем базу данных
echo Creating database '%db_name%'...
echo.

REM Используем psql с командой через -c
REM Пробуем сначала через локальный сокет (без -h)
if not "!db_password!"=="" (
    set "PGPASSWORD=!db_password!"
)
%PSQL_CMD% -U %db_user% -p %db_port% -c "CREATE DATABASE %db_name%;" 2>&1
if errorlevel 1 (
    REM Если не получилось, пробуем через TCP/IP
    echo Trying TCP/IP connection...
    if not "!db_password!"=="" (
        set "PGPASSWORD=!db_password!"
    )
    %PSQL_CMD% -U %db_user% -h %db_host% -p %db_port% -c "CREATE DATABASE %db_name%;" 2>&1
)

REM Проверяем результат последней команды
%PSQL_CMD% -U %db_user% -p %db_port% -lqt 2>nul | findstr /i "%db_name%" >nul
if errorlevel 1 (
    %PSQL_CMD% -U %db_user% -h %db_host% -p %db_port% -lqt 2>nul | findstr /i "%db_name%" >nul
)

if errorlevel 1 (
    echo.
    echo [FAIL] Failed to create database!
    echo.
    echo Diagnostic information:
    echo.
    echo Checking PostgreSQL service status...
    sc query postgresql-x64-16 2>nul | findstr /i "STATE" >nul
    if !errorlevel!==0 (
        echo   Service status: Running (but may not be accepting connections)
    ) else (
        echo   Service status: Not found or not running
    )
    echo.
    echo Checking if port %db_port% is listening...
    netstat -an | findstr ":%db_port%" >nul
    if !errorlevel!==0 (
        echo   Port %db_port%: Listening
    ) else (
        echo   Port %db_port%: Not listening (PostgreSQL may not be accepting TCP/IP connections)
    )
    echo.
    echo Possible reasons:
    echo   - PostgreSQL service is running but PostgreSQL process crashed
    echo   - PostgreSQL is not configured to accept TCP/IP connections
    echo   - PostgreSQL data directory may be corrupted
    echo   - Incorrect username or password
    echo   - Insufficient permissions
    echo.
    echo Solutions to try:
    echo   1. Restart PostgreSQL service:
    echo      net stop postgresql-x64-16
    echo      net start postgresql-x64-16
    echo.
    echo   2. Check PostgreSQL logs in:
    echo      C:\Program Files\PostgreSQL\16\data\log\
    echo.
    echo   3. Try connecting manually:
    echo      psql -U %db_user% -p %db_port%
    echo      CREATE DATABASE %db_name%;
    echo      \q
    echo.
    pause
    exit /b 1
)

REM Проверяем создание базы данных
echo Verifying database creation...
if not "!db_password!"=="" (
    set "PGPASSWORD=!db_password!"
)
%PSQL_CMD% -U %db_user% -p %db_port% -lqt 2>nul | findstr /i "%db_name%" >nul
if %errorlevel% neq 0 (
    REM Попробуем через TCP/IP
    if not "!db_password!"=="" (
        set "PGPASSWORD=!db_password!"
    )
    %PSQL_CMD% -U %db_user% -h %db_host% -p %db_port% -lqt 2>nul | findstr /i "%db_name%" >nul
)
if %errorlevel%==0 (
    echo [OK] Database verification successful!
    echo.
    echo Database '%db_name%' is ready to use.
) else (
    echo [WARN] Database created but verification failed.
    echo Please check manually.
)

echo.
pause
exit /b 0







