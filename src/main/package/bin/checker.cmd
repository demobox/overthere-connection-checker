@echo off
REM
REM Batch script to start the Overthere Connection Checker
REM

setlocal ENABLEDELAYEDEXPANSION

REM Get Java executable
if "%JAVA_HOME%"=="" (
  set JAVACMD=java
) else (
  set JAVACMD="%JAVA_HOME%\bin\java"
)

REM Get checker home dir
if "%CHECKER_HOME%"=="" (
  cd /d "%~dp0"
  cd ..
  set CHECKER_HOME=!CD!
)

cd /d "%CHECKER_HOME%"

REM Build checker classpath
set CHECKER_CLASSPATH=conf
for %%i in (lib\*.jar) do set CHECKER_CLASSPATH=!CHECKER_CLASSPATH!;%%i

REM Run checker
%JAVACMD% -cp "%CHECKER_CLASSPATH%" com.xebialabs.deployit.overthere.ConnectionChecker %*

:end
endlocal
