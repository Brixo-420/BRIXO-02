@echo off
setlocal

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  mvn %*
  exit /b %ERRORLEVEL%
)

set "MAVEN_HOME=C:\Tools\maven\apache-maven-3.9.9"
if exist "%MAVEN_HOME%\bin\mvn.cmd" (
  "%MAVEN_HOME%\bin\mvn.cmd" %*
  exit /b %ERRORLEVEL%
)

echo Maven no se encuentra. Instala Maven o agregalo al PATH.
exit /b 1
