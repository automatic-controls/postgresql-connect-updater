@echo off
if /i "%*" EQU "--help" (
  echo PUBLISH           Copies the .addon archive to the resources folder of postgresql-connector.
  exit /b 0
)
if "%*" NEQ "" (
  echo Unexpected parameter.
  exit /b 1
)
if not exist "%addonFile%" (
  echo Cannot publish because !name!.addon does not exist.
  exit /b 1
)
copy /y "%addonFile%" "%~dp0..\..\postgresql-connect\src\aces\webctrl\postgresql\resources\!name!.addon" >nul
if %ErrorLevel% EQU 0 (
  echo Publish successful.
  exit /b 0
) else (
  echo Publish unsuccessful.
  exit /b 1
)