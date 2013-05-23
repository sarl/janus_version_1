@ECHO OFF

for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    echo Output: %%g
    set JAVAVER=%%g
)
set JAVAVER=%JAVAVER:"=%

for /f "delims=. tokens=1-3" %%v in ("%JAVAVER%") do (
    set JAVAMAJORVER=%%v
    set JAVAMINORVER=%%w
    set JAVABUILDVER=%%x
)

if "%JAVAMAJORVER%"=="1" goto step1:
goto errorcase

:step1
if "%JAVAMINORVER%"=="6" goto step2:
goto errorcase

:step2
cd %~dp0\janus
mvn -Dmaven.test.skip=true clean install
cd %~dp0\janus-ui
mvn -Dmaven.test.skip=true clean install
exit 0

:errorcase
echo "ERROR: you must use a JDK 1.6 to compile the Janus platform,"
echo "ERROR: because several parts of the platform are not compatible "
echo "ERROR: newver versions (eg. Android modules)."
exit 1

