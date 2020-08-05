::----------------------------------------------------------------------
@echo off
::----------------------------------------------------------------------

:: IF NOT EXIST "%JAVA_EXE%" goto error

:error
echo ---------------------------------------------------------------------
echo WARNING: JAVA_HOME not set.. defaulting to 'java.exe'.
echo Please make sure 'sted.bat' is edited to point to the correct installation directories.
echo ---------------------------------------------------------------------
SET JAVA_EXE=java.exe

:: ---------------------------------------------------------------------
:: Set the JAVA Installation Directory
:: ---------------------------------------------------------------------
SET JAVA_HOME="C:\Program Files\Java\jdk-14.0.2"

:: ---------------------------------------------------------------------
:: JAVA executable.. set from JAVA_HOME
:: ---------------------------------------------------------------------
SET JAVA_EXE=%JAVA_HOME%\bin\java.exe


:: ---------------------------------------------------------------------
:: You may specify your own JVM arguments in STED_JVM_ARGS variable.
:: ---------------------------------------------------------------------
::set STED_JVM_ARGS=-Xms16m -Xmx64m -Dsun.java2d.nodraw=true
:: SET STED_JVM_ARGS="-Xms128m -Xmx512m"
SET STED_JVM_ARGS=

:: ---------------------------------------------------------------------
:: Before you run STED specify the location of the
:: directory where STED is installed
:: In most cases you do not need to change the settings below.
:: ---------------------------------------------------------------------
SET STED_HOME=.

:: ---------------------------------------------------------------------
:: Set STED path to customize
:: ---------------------------------------------------------------------
:: path to user settings
set STED_SETTINGS_PATH=%STED_HOME%/settings
:: path to images
set STED_ICON_PATH=%STED_HOME%/images
:: path to resource
set STED_RESOURCE_PATH=%STED_HOME%/resource
:: path to log
set STED_LOG_PATH=%STED_HOME%/log

:: ---------------------------------------------------------------------
:: IF RUNNING CONSOLE.. SET CONSOLE DEPENDENT SYSTEM PROPERTIES
:: ---------------------------------------------------------------------
SET STED_CONSOLE_JVM_ARGS=-"Dfontmap.file=fontmap.xml -Dinput.file=input.txt -Doutput.file=output.txt"

:: ---------------------------------------------------------------------
:: NO NEED TO CHANGE ANYTHING BELOW
:: ---------------------------------------------------------------------
:: SET JVM_ARGS= %STED_JVM_ARGS% -Dsted.home.path=%STED_HOME% -Dsted.icon.path=%STED_ICON_PATH% -Dsted.settings.path=%STED_SETTINGS_PATH% -Dsted.resource.path=%STED_RESOURCE_PATH% -Dsted.log.path=%STED_LOG_PATH%
SET JVM_ARGS=%STED_JVM_ARGS%

SET OLD_PATH=%PATH%
SET PATH=%STED_HOME%;%PATH%

:: SET CLASS_PATH="%STED_HOME%\bin\sted.jar;%STED_HOME%\bin\sted-widgets.jar;%STED_HOME%"
:: IF NOT EXIST "%CLASS_PATH%" goto lib_error

:: ---------------------------------------------------------------------
:: STED Main Class
:: ---------------------------------------------------------------------
:: SET MAIN_CLASS_NAME=intellibitz.sted.Main

:: ---------------------------------------------------------------------
:: Launch STED GUI
:: ---------------------------------------------------------------------
:: "%JAVA_EXE%" %JVM_ARGS% -cp "%CLASS_PATH%" %MAIN_CLASS_NAME% %::
%JAVA_EXE% %JVM_ARGS% -jar sted.jar %*

:: ---------------------------------------------------------------------
:: Launch STED Console
:: ---------------------------------------------------------------------
:: SET JVM_ARGS= %STED_CONSOLE_JVM_ARGS% %JVM_ARGS%
:: "%JAVA_EXE%" %JVM_ARGS% -cp "%CLASS_PATH%" %MAIN_CLASS_NAME% -c %::

SET PATH=%OLD_PATH%

goto end


pause
goto end

:lib_error
echo ---------------------------------------------------------------------
echo ERROR: cannot start STED - 'sted.jar' NOT FOUND.
echo Please make sure 'sted.jar' is in the 'bin' folder.
echo ---------------------------------------------------------------------

pause
:end
