@echo off
cd /d "%~dp0"
echo Borrando carpeta target...
if exist target rmdir /s /q target
echo Compilando y arrancando con Maven...
call mvnw.cmd clean spring-boot:run
pause
