@echo off
echo ============================================
echo     EMPAQUETANDO APP CON jpackage
echo ============================================

jpackage ^
  --type exe ^
  --input app ^
  --name CatsApp ^
  --main-jar cats-0.0.1-SNAPSHOT.jar ^
  --main-class com.cats.cats.Main ^
  --runtime-image jlink-jdk ^
  --win-console

echo.
echo ============================================
echo     ¡Instalador CatsApp.exe generado!
echo ============================================
pause