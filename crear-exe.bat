@echo off
echo ============================================
echo   Generando instalador con jpackage...
echo ============================================

:: Ruta relativa a los .jar de JavaFX
set JAVAFX_LIB=lib

:: Ejecutar jpackage
jpackage ^
  --type exe ^
  --input app ^
  --name CatsApp ^
  --main-jar cats-0.0.1-SNAPSHOT.jar ^
  --main-class com.cats.cats.Main ^
  --java-options "--module-path %JAVAFX_LIB% --add-modules javafx.controls,javafx.fxml,javafx.media" ^
  --win-console

echo.
echo ============================================
echo   ¡Listo! Revisa el archivo CatsApp.exe
echo ============================================
pause