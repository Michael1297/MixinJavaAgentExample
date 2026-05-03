# Запуск Spring-демо с Java-agent (Mixin)

Агент — JAR из корня **`MixinJavaAgentExample`**, рядом с ним обязателен каталог **`target/lib/`** (после `mvn package`).

## Сборка

Из корня `MixinJavaAgentExample`:

```bash
mvn -q install
cd spring
mvn -q package
```

## Запуск с агентом

Из **корня** репозитория (чтобы относительные пути к `lib/` у агентского JAR совпали с манифестом):

**Windows** — из папки `spring-agent`:

```bat
run-spring-with-agent.cmd
```

**Вручную:**

```bash
java -javaagent:target/MixinJavaAgentExample-1.0-SNAPSHOT.jar ^
  -jar spring/target/mixin-spring-demo-1.0-SNAPSHOT.jar
```

Linux/macOS:

```bash
java -javaagent:target/MixinJavaAgentExample-1.0-SNAPSHOT.jar \
  -jar spring/target/mixin-spring-demo-1.0-SNAPSHOT.jar
```

## Проверка

`http://localhost:8080/demo` — с агентом поля вроде `message`, `walletCoins`, `pricer`, `banner` совпадают с трансформированным демо (см. корневой `Main` / `MIXIN_RU.md`).

## Без агента

```bash
java -jar spring/target/mixin-spring-demo-1.0-SNAPSHOT.jar
```
