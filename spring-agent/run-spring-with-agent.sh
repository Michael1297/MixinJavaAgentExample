#!/usr/bin/env sh
set -e
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
AGENT_JAR="$ROOT/target/MixinJavaAgentExample-1.0-SNAPSHOT.jar"
SPRING_JAR="$ROOT/spring/target/mixin-spring-demo-1.0-SNAPSHOT.jar"
test -f "$AGENT_JAR" || { echo "Build root: (cd \"$ROOT\" && mvn -q package)"; exit 1; }
test -d "$ROOT/target/lib" || { echo "Missing target/lib — mvn package in root"; exit 1; }
test -f "$SPRING_JAR" || { echo "Build spring: (cd \"$ROOT/spring\" && mvn -q package)"; exit 1; }
exec java -javaagent:"$AGENT_JAR" -jar "$SPRING_JAR"
