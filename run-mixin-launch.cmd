@echo off
setlocal
cd /d "%~dp0"
call mvn -q package
if errorlevel 1 exit /b 1
java -cp "target\MixinJavaAgentExample-1.0-SNAPSHOT.jar;target\lib\*" net.minecraft.launchwrapper.Launch --tweakClass io.github.michael1297.launch.MixinDemoTweaker
pause
