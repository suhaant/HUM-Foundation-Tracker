@echo off
if not exist out mkdir out

javac --module-path lib --add-modules javafx.controls -d out src\hum\*.java

if %ERRORLEVEL% == 0 (
    echo.
    echo Compiled successfully. Run:  run.bat
) else (
    echo.
    echo Compilation failed.
    echo Make sure Java 17+ is installed and JavaFX JARs are in the lib\ folder.
    pause
)
