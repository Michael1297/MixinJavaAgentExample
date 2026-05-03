# Подключение агента Mixin (`MixinJavaAgentExample`) к Apache Tomcat

Речь о том, чтобы **процесс JVM Tomcat** стартовал с **`-javaagent`**: тогда срабатывает `Premain-Class` из собранного JAR (`io.github.michael1297.agent.MixinPremain`) и миксины могут менять байткод классов, которые позже загрузит Tomcat (в том числе классы из вашего WAR).

Это **не** «установка WAR как приложения из этого репозитория»: сам репозиторий — демо-библиотека агента. В Tomcat вы, как правило, кладёте **свой** веб-проект и **отдельно** подключаете уже собранный **JAR агента** + каталог **`lib/`** к JVM.

## 1. Сборка артефакта агента

В корне проекта:

```bash
mvn -q package
```

Нужны:

- `target/MixinJavaAgentExample-1.0-SNAPSHOT.jar` — JAR с манифестом (`Premain-Class`, `MixinConfigs`, `Class-Path: lib/...`);
- `target/lib/` — все зависимости (mixin, gson, guava, launchwrapper и т.д.).

**Важно:** в манифесте classpath к зависимостям **относительный** (`lib/…`). Каталог `lib` должен лежать **рядом с JAR агента** в той файловой структуре, куда вы скопируете артефакты на сервер (как после `package` в `target/`).

### Где на диске лежит агент и как это связано с Tomcat

**`/opt/mixin-agent/` в документе — только пример имени папки.** Это **отдельная** директория на сервере, не обязанная совпадать с установкой Tomcat. Вы можете назвать её как угодно, например:

- `/opt/mixin-agent/` — рядом с другими «служебными» приложениями;
- `/opt/tomcat/mixin-agent/` — внутри дерева Tomcat, если так удобнее при `CATALINA_HOME=/opt/tomcat`;
- любой другой путь (`/srv/agents/mixin/`, и т.д.).

**Tomcat** (у вас, например, **`/opt/tomcat`**) и **папка с JAR агента + `lib/`** — это две разные вещи:

| Что | Назначение |
|-----|------------|
| **`/opt/tomcat`** | `CATALINA_HOME` / `CATALINA_BASE`: бинарники Tomcat, `webapps/`, `conf/`, сюда же кладёте **`bin/setenv.sh`** с `CATALINA_OPTS`. |
| **Папка с агентом** | Только файлы **MixinJavaAgentExample-…jar** и **`lib/*.jar`**. Сюда копируете результат `mvn package`, Tomcat сам эту папку не «устанавливает». |

В `setenv.sh` вы **прописывают абсолютный путь** к JAR агента; Tomcat при старте подхватит опцию `-javaagent:…`. Каталог Tomcat и каталог агента могут быть где угодно друг от друга — главное, чтобы путь в `-javaagent` был верным и рядом с агентским JAR лежал **`lib/`**.

Пример раскладки (один из вариантов):

```text
/opt/mixin-agent/                    ← отдельная папка только под агент (пример)
  MixinJavaAgentExample-1.0-SNAPSHOT.jar
  lib/
    gson-…jar
    guava-…jar
    mixin-…jar
    launchwrapper-…jar
    …

/opt/tomcat/                         ← ваш Tomcat (пример)
  bin/setenv.sh                      ← здесь добавляете CATALINA_OPTS с -javaagent
  webapps/
  …
```

Путь к JAR в `-javaagent` должен быть **абсолютным** — так проще не ошибиться при разных рабочих каталогах Tomcat.

## 2. Куда прописать `-javaagent` в Tomcat

Редактировать `catalina.bat` / `catalina.sh` напрямую **не рекомендуется**. Используйте:

- **`$CATALINA_BASE/bin/setenv.sh`** (Linux/macOS) или **`setenv.bat`** (Windows),
- либо переменные окружения до запуска, если у вас так принято.

В **`setenv.sh`** (файл лежит в **`$CATALINA_BASE/bin`** или **`$CATALINA_HOME/bin`**, у типичной установки с одним инстансом часто **`/opt/tomcat/bin/setenv.sh`**):

```bash
#!/bin/sh
# Папка с агентом — любая; не путать с CATALINA_HOME (ниже — пример с Tomcat в /opt/tomcat)
AGENT_DIR=/opt/mixin-agent
AGENT_JAR="$AGENT_DIR/MixinJavaAgentExample-1.0-SNAPSHOT.jar"
export CATALINA_OPTS="$CATALINA_OPTS -javaagent:$AGENT_JAR"
```

В **`setenv.bat`** (Windows, путь подставьте свой):

```bat
set "AGENT_DIR=C:\apps\mixin-agent"
set "AGENT_JAR=%AGENT_DIR%\MixinJavaAgentExample-1.0-SNAPSHOT.jar"
set "CATALINA_OPTS=%CATALINA_OPTS% -javaagent:%AGENT_JAR%"
```

Перезапустите Tomcat. В логе старта JVM в списке аргументов должно появиться `-javaagent:...`.

`JAVA_OPTS` тоже сработает для дочернего процесса, но в документации Tomcat чаще используют **`CATALINA_OPTS`** именно для опций, относящихся к этому инстансу.

## 3. Свой WAR и цели миксинов

Демо-конфиг **`mixins.demo.json`** в этом проекте нацелен на классы вроде `io.github.michael1297.demo.Greeter`, `com.thirdparty.demo.BusinessLogic` и т.д. Чтобы миксины имели смысл в Tomcat:

- либо в WAR должны попасть **те же** классы (те же пакеты и имена), что и в конфиге;
- либо вы собираете **свой** JAR агента с **другим** `mixins.*.json` и своими mixin-классами под ваше приложение.

Иначе агент поднимется, но трансформаций для ваших сервлетов не будет.

## 4. Ограничения и типичные проблемы

| Тема | Комментарий |
|------|-------------|
| **Область действия** | Агент вешается на **весь** JVM-процесс Tomcat: все веб-приложения и общие классы, которые загружаются после `premain`. |
| **Порядок загрузки** | Классы, успевшие загрузиться до регистрации трансформера, не «починятся» сами по себе без retransform. Для веб-приложений обычно достаточно того, что WAR грузится после старта JVM. |
| **JDK 9+** | Если на рефлексию к приватному конструктору `Launch` ругнётся модульная система, в `CATALINA_OPTS` добавьте нужные **`--add-opens`** (точный набор зависит от версии JDK и политики). На Java 8 обычно ничего не нужно. |
| **Логи Log4j из LaunchWrapper** | При старте возможны сообщения про `Queue` / `ServerGuiConsole` из `log4j2.xml` внутри `launchwrapper` — это не ошибка Tomcat и не мешает работе агента (см. обсуждение в [MIXIN_RU.md](./MIXIN_RU.md)). |
| **Безопасность** | Агент меняет байткод — подключайте только доверенные JAR и с контролируемым CI. |

## 5. Проверка, что агент реально подключён

- В логе catalina при старте процесса (или через `jcmd <pid> VM.flags` / аналог) убедитесь, что в командной строке JVM есть `-javaagent:...`.
- Для отладки можно временно добавить в свой код явный вывод или точку останова в **загрузчике классов** после деплоя WAR и убедиться, что ожидаемые классы проходят через вашу цепочку миксинов (см. исходники `MixinDemoPremain`, `LaunchWrapperTransformerBridge`).

## 6. Связь с документацией проекта

Общая схема Mixin, LaunchWrapper и `-javaagent` в этом репозитории описана в [MIXIN_RU.md](./MIXIN_RU.md). Аннотации миксинов — в [MIXIN_ANNOTATIONS_RU.md](./MIXIN_ANNOTATIONS_RU.md).
