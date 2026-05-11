@echo off
if not exist out mkdir out
if not exist out\hum mkdir out\hum

echo Compiling...
javac --module-path lib --add-modules javafx.controls -d out src\hum\*.java

if %ERRORLEVEL% == 0 (
    copy src\hum\style.css out\hum\style.css >nul
    echo Compiled successfully. Run:  run.bat
) else (
    echo Compilation failed.
    echo Make sure Java 17+ is installed and JavaFX JARs are in the lib\ folder.
    pause
)
