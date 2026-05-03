@echo off
setlocal
cd /d "%~dp0\.."
if not exist "target\MixinJavaAgentExample-1.0-SNAPSHOT.jar" (
  echo Build root first: mvn -q package
  exit /b 1
)
if not exist "target\lib\mixin-0.8.7.jar" (
  echo Missing target\lib — run mvn -q package in repo root
  exit /b 1
)
if not exist "spring\target\mixin-spring-demo-1.0-SNAPSHOT.jar" (
  echo Build spring: cd spring ^&^& mvn -q package
  exit /b 1
)
java -javaagent:"%cd%\target\MixinJavaAgentExample-1.0-SNAPSHOT.jar" -jar "%cd%\spring\target\mixin-spring-demo-1.0-SNAPSHOT.jar"
