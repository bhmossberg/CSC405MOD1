@echo off
REM Build and run the CSC405 Module 1 environment tests on Windows.
REM CLASSPATH uses the Windows semicolon separator.
REM
REM Usage (from the repo root, in cmd.exe):
REM   set CSC405_LIB=C:\JOGL
REM   set CSC405_JOML=C:\JOML
REM   run.bat compile
REM   run.bat jdk
REM   run.bat info
REM   run.bat shader
REM   run.bat rotate
REM   run.bat info -autoClose 2500
REM
REM The textbook (Ch. 2, after Program 2.1) recommends these Java2D flags on
REM Windows: -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1

setlocal EnableExtensions EnableDelayedExpansion

cd /d "%~dp0"

if not defined CSC405_LIB (
  if exist "%~dp0..\..\libs\jogl-all.jar" (
    set "CSC405_LIB=%~dp0..\..\libs"
  ) else if exist "C:\JOGL\jar\jogl-all.jar" (
    set "CSC405_LIB=C:\JOGL\jar"
  ) else if exist "C:\JOGL\jogl-all.jar" (
    set "CSC405_LIB=C:\JOGL"
  )
)
if not defined CSC405_JOML (
  if exist "%CSC405_LIB%\joml-1.10.0.jar" (
    set "CSC405_JOML=%CSC405_LIB%"
  ) else if exist "C:\JOML\joml-1.10.0.jar" (
    set "CSC405_JOML=C:\JOML"
  )
)

if not exist "%CSC405_LIB%\jogl-all.jar" (
  echo Set CSC405_LIB to the folder that holds jogl-all.jar and gluegen-rt.jar.
  echo Example: set CSC405_LIB=C:\JOGL
  exit /b 1
)
if not exist "%CSC405_JOML%\joml-1.10.0.jar" (
  echo Set CSC405_JOML to the folder that holds joml-1.10.0.jar.
  echo Example: set CSC405_JOML=C:\JOML
  exit /b 1
)

set "CP=%CSC405_LIB%\jogl-all.jar;%CSC405_LIB%\gluegen-rt.jar;%CSC405_JOML%\joml-1.10.0.jar"
if exist "%CSC405_LIB%\jogl-all-natives-windows-amd64.jar" set "CP=%CP%;%CSC405_LIB%\jogl-all-natives-windows-amd64.jar"
if exist "%CSC405_LIB%\gluegen-rt-natives-windows-amd64.jar" set "CP=%CP%;%CSC405_LIB%\gluegen-rt-natives-windows-amd64.jar"

set "JAVAC=javac"
set "JAVA=java"
if defined JAVA_HOME (
  if exist "%JAVA_HOME%\bin\javac.exe" set "JAVAC=%JAVA_HOME%\bin\javac.exe"
  if exist "%JAVA_HOME%\bin\java.exe" set "JAVA=%JAVA_HOME%\bin\java.exe"
)

set "CMD=%~1"
if "%CMD%"=="" set "CMD=compile"
if not "%~1"=="" shift

if /I "%CMD%"=="compile" goto COMPILE
if /I "%CMD%"=="jdk" goto JDK
if /I "%CMD%"=="info" goto INFO
if /I "%CMD%"=="shader" goto SHADER
if /I "%CMD%"=="rotate" goto ROTATE
echo Usage: run.bat compile^|jdk^|info^|shader^|rotate [-autoClose [millis]] [-triangle]
exit /b 2

:COMPILE
if not exist out mkdir out
echo Compiling with %JAVAC% (--release 11)
"%JAVAC%" --release 11 -d out -cp "%CP%" src\*.java
if errorlevel 1 exit /b 1
echo Compiled classes are in out\ (gitignored).
goto :EOF

:JDK
call :COMPILE
if errorlevel 1 exit /b 1
"%JAVA%" -cp "out;%CP%" JdkJomlCheck %*
goto :EOF

:INFO
call :COMPILE
if errorlevel 1 exit /b 1
"%JAVA%" -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 -cp "out;%CP%" Program21 %*
goto :EOF

:SHADER
call :COMPILE
if errorlevel 1 exit /b 1
"%JAVA%" -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 -cp "out;%CP%" Program22 %*
goto :EOF

:ROTATE
call :COMPILE
if errorlevel 1 exit /b 1
"%JAVA%" -Dsun.java2d.d3d=false -Dsun.java2d.uiScale=1 -cp "out;%CP%" RotatingTriangle %*
goto :EOF
