# Spring Boot + те же демо-классы (Mixin)

Модуль **`mixin-spring-demo`** зависит от **`MixinJavaAgentExample`** (классы + `mixins.demo.json` в том же JAR) и отдаёт **`GET /demo`**.

## Сборка

```bash
cd ..   # корень MixinJavaAgentExample
mvn -q install
cd spring
mvn -q package
```

(`install` в корне выполняет `package`: рядом с JAR агента должен лежать **`target/lib/`** для `Class-Path` в манифесте.)

## Запуск без агента

```bash
java -jar target/mixin-spring-demo-1.0-SNAPSHOT.jar
```

`http://localhost:8080/demo` — без `-javaagent` миксины не применяются.

## С агентом

См. [../spring-agent/README.md](../spring-agent/README.md).
