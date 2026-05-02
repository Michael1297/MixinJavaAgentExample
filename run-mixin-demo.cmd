@echo off
setlocal
cd /d "%~dp0"
call mvn -q package
if errorlevel 1 exit /b 1
java -javaagent:target\MixinJavaAgentExample-1.0-SNAPSHOT.jar -cp "target\MixinJavaAgentExample-1.0-SNAPSHOT.jar;target\lib\*" io.github.michael1297.Main
pause