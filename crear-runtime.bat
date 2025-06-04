@echo off
echo ============================================
echo     CREANDO JDK REDUCIDO CON JLINK
echo ============================================

:: Ruta a los módulos de JavaFX
set JAVAFX_MODS=javafx-jmods-21.0.7

:: Ruta a los módulos estándar de Java
set JDK_MODS=%JAVA_HOME%\jmods

:: Crear runtime con jlink
jlink ^
  --module-path "%JAVAFX_MODS%;%JDK_MODS%" ^
  --add-modules java.base,java.logging,java.xml,java.desktop,javafx.controls,javafx.fxml,javafx.media ^
  --output jlink-jdk ^
  --strip-debug --no-header-files --no-man-pages --compress=2

echo.
echo ============================================
echo     ✅ JDK REDUCIDO CREADO EN jlink-jdk
echo ============================================
pause