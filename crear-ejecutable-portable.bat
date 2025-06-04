@echo off
echo ============================================
echo     CREANDO EJECUTABLE PORTÁTIL
echo ============================================

jpackage ^
  --type app-image ^
  --input app ^
  --name CatsApp ^
  --main-jar cats-0.0.1-SNAPSHOT.jar ^
  --main-class com.cats.cats.Main ^
  --runtime-image jlink-jdk ^
  --win-console

echo.
echo ============================================
echo     ✅ Ejecutable generado en carpeta CatsApp\
echo ============================================
pause