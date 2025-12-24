@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo ========================================
echo PostgreSQL Setup Helper
echo ========================================
echo.

REM Поиск всех postgresql*.jar файлов в Maven репозитории
set "MAVEN_REPO=%USERPROFILE%\.m2\repository"
set "POSTGRESQL_PATH=%MAVEN_REPO%\org\postgresql\postgresql"

echo [1/3] Searching for PostgreSQL JDBC drivers...
echo Searching in: %POSTGRESQL_PATH%
echo.

set "jar_count=0"
set "jar_index=0"

REM Создаем временный файл для хранения путей
set "temp_file=%TEMP%\postgresql_jars_%RANDOM%.txt"
del "%temp_file%" 2>nul

REM Ищем все postgresql*.jar файлы
for /r "%POSTGRESQL_PATH%" %%f in (postgresql*.jar) do (
    if exist "%%f" (
        set /a jar_count+=1
        echo !jar_count!. %%f
        echo %%f>>"%temp_file%"
    )
)

if %jar_count%==0 (
    echo No PostgreSQL JDBC drivers found in Maven repository.
    echo Expected location: %POSTGRESQL_PATH%
    echo.
    goto :find_services
)

echo.
echo Found %jar_count% PostgreSQL JDBC driver(s)
echo.

REM Поиск установленных служб PostgreSQL
:find_services
echo [2/3] Searching for installed PostgreSQL services...
echo.

set "service_count=0"
set "service_index=0"

REM Создаем временный файл для хранения имен служб
set "services_file=%TEMP%\postgresql_services_%RANDOM%.txt"
del "%services_file%" 2>nul

REM Получаем список всех служб PostgreSQL
for /f "tokens=1*" %%a in ('sc query type^= service state^= all ^| findstr /i "postgresql"') do (
    for /f "tokens=1" %%s in ('sc query "%%b" ^| findstr /i "SERVICE_NAME"') do (
        set "service_name=%%b"
        set "service_name=!service_name: =!"
        if not "!service_name!"=="" (
            set /a service_count+=1
            echo !service_count!. !service_name!
            echo !service_name!>>"%services_file%"
        )
    )
)

REM Альтернативный способ поиска служб
if %service_count%==0 (
    echo Trying alternative method...
    for /f "tokens=*" %%s in ('sc query state^= all ^| findstr /i /c:"postgresql"') do (
        echo Found: %%s
    )
    
    REM Пробуем стандартные имена служб
    echo.
    echo Trying common PostgreSQL service names...
    set "common_services=postgresql-x64-15 postgresql-x64-16 postgresql-x64-14 postgresql-x64-13 postgresql-x64-12 postgresql-x64-11"
    for %%s in (%common_services%) do (
        sc query "%%s" >nul 2>&1
        if !errorlevel!==0 (
            set /a service_count+=1
            echo !service_count!. %%s
            echo %%s>>"%services_file%"
        )
    )
)

if %service_count%==0 (
    echo No PostgreSQL services found.
    echo.
    echo Please install PostgreSQL or check if it's running with a different name.
    echo You can check all services with: sc query type^= service state^= all
    echo.
    goto :select_action
) else (
    echo.
    echo Found %service_count% PostgreSQL service(s)
    echo.
)

:select_action
echo [3/3] Select action:
echo.
echo 1. Show PostgreSQL JDBC driver information
echo 2. Start PostgreSQL service
echo 3. Stop PostgreSQL service
echo 4. Check PostgreSQL service status
echo 5. Exit
echo.
set /p "choice=Enter your choice (1-5): "

if "%choice%"=="1" goto :show_jars
if "%choice%"=="2" goto :start_service
if "%choice%"=="3" goto :stop_service
if "%choice%"=="4" goto :check_status
if "%choice%"=="5" goto :end
goto :select_action

:show_jars
echo.
echo PostgreSQL JDBC Drivers found:
echo.
set "line_num=0"
for /f "usebackq delims=" %%a in ("%temp_file%") do (
    set /a line_num+=1
    echo !line_num!. %%a
)
echo.
pause
goto :select_action

:start_service
if %service_count%==0 (
    echo No PostgreSQL services found to start.
    pause
    goto :select_action
)

if %service_count%==1 (
    REM Если только одна служба, используем её
    for /f "usebackq delims=" %%s in ("%services_file%") do (
        set "selected_service=%%s"
        goto :start_selected
    )
) else (
    echo.
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
            goto :start_selected
        )
    )
    echo Invalid selection.
    pause
    goto :select_action
)

:start_selected
echo.
echo Starting PostgreSQL service: %selected_service%
echo.
net start "%selected_service%"
if %errorlevel%==0 (
    echo.
    echo [OK] PostgreSQL service started successfully!
    echo.
    echo Service: %selected_service%
    echo Status: Running
) else (
    echo.
    echo [FAIL] Failed to start PostgreSQL service.
    echo Error code: %errorlevel%
    echo.
    echo Possible reasons:
    echo - Service is already running
    echo - Insufficient permissions (run as Administrator)
    echo - Service is disabled
    echo.
    echo To check service status, use option 4.
)
echo.
pause
goto :select_action

:stop_service
if %service_count%==0 (
    echo No PostgreSQL services found to stop.
    pause
    goto :select_action
)

if %service_count%==1 (
    for /f "usebackq delims=" %%s in ("%services_file%") do (
        set "selected_service=%%s"
        goto :stop_selected
    )
) else (
    echo.
    echo Select PostgreSQL service to stop:
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
            goto :stop_selected
        )
    )
    echo Invalid selection.
    pause
    goto :select_action
)

:stop_selected
echo.
echo Stopping PostgreSQL service: %selected_service%
echo.
net stop "%selected_service%"
if %errorlevel%==0 (
    echo.
    echo [OK] PostgreSQL service stopped successfully!
) else (
    echo.
    echo [FAIL] Failed to stop PostgreSQL service.
    echo Error code: %errorlevel%
)
echo.
pause
goto :select_action

:check_status
if %service_count%==0 (
    echo No PostgreSQL services found.
    pause
    goto :select_action
)

echo.
echo PostgreSQL Service Status:
echo ========================================
echo.

for /f "usebackq delims=" %%s in ("%services_file%") do (
    echo Service: %%s
    sc query "%%s" | findstr /i "STATE"
    echo.
)

pause
goto :select_action

:end
REM Очистка временных файлов
del "%temp_file%" 2>nul
del "%services_file%" 2>nul
echo.
echo Exiting...
exit /b 0







