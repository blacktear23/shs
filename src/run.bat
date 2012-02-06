@echo off
set JAVA=java.exe
set CURRENT_DIR=%cd%
set MAIN_CLASS=org.lyl.simplehttpserver.Launcher
set CLASS_PATH=%CURRENT_DIR%\lib\*
"%JAVA%" -cp "%CLASS_PATH%" %MAIN_CLASS%
:end