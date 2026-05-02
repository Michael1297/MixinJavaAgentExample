# Sponge Mixin и «агент» JVM: что возможно

Официальный репозиторий: [SpongePowered/Mixin](https://github.com/SpongePowered/Mixin).

## Можно ли использовать Mixin как `-javaagent`?

**Частично.** В репозитории есть класс [`org.spongepowered.tools.agent.MixinAgent`](https://github.com/SpongePowered/Mixin/blob/master/src/agent/java/org/spongepowered/tools/agent/MixinAgent.java) с методами **`premain`** и **`agentmain`**, которые регистрируют `Instrumentation` и трансформер для **повторного применения миксинов / hot-swap** (см. Javadoc в исходниках: «re-transforms a mixin's target classes if the mixin has been redefined»).

Это **не** полноценная замена сценарию «один fat-jar с `Premain-Class`, как у ClassTransform»: для **первичного** запуска подсистемы Mixin на JVM Sponge historically опирается на **хост-платформу**:

- **LegacyLauncher** (`net.minecraft.launchwrapper`) + **`MixinTweaker`**;
- либо **ModLauncher** (`cpw.mods.modlauncher`) и соответствующие SPI.

В поставляемом JAR `mixin` в `META-INF/services/org.spongepowered.asm.service.IMixinService` перечислены сервисы **LaunchWrapper** и **ModLauncher**; отдельного «чистого AppClassLoader»-сервиса нет, поэтому **холодный старт** миксинов на обычном `java -jar …` без одной из этих оболочек **не поддержан из коробки**.

### Этот пример (`MixinJavaAgentExample`)

Используется **тот же набор целевых классов**, что и в `ClassTransformExample`, а миксины подключаются через:

1. **`net.minecraft.launchwrapper.Launch`** (артефакт `net.minecraft:launchwrapper:1.12` с `libraries.minecraft.net`, без LWJGL);
2. кастомный tweaker **`io.github.michael1297.launch.MixinDemoTweaker`**, который вызывает `MixinBootstrap.start()` / `doInit` / `inject()` (для `start`/`inject` используется **рефлексия**, т.к. в Mixin 0.8.7 эти методы **package-private**, а размещение tweaker в пакете `org.spongepowered.asm.launch` даёт **`SecurityException` из‑за подписи JAR Mixin**);
3. конфиг **`mixins.demo.json`** и манифест **`MixinConfigs: mixins.demo.json`** в проектном JAR.

Так вы получаете сопоставимое с ClassTransform-примером поведение **на старте JVM**, но точка входа — **Launch**, а не `Main` напрямую.

## Сборка и запуск

```bash
mvn -q package
```

Зависимости копируются в `target/lib/`. Запуск (Windows, одна строка):

```bat
java -cp "target\MixinJavaAgentExample-1.0-SNAPSHOT.jar;target\lib\*" net.minecraft.launchwrapper.Launch --tweakClass io.github.michael1297.launch.MixinDemoTweaker
```

Либо после `mvn compile` (когда все артефакты уже в локальном репозитории Maven):

```bash
mvn -q compile exec:exec
```

(см. `exec-maven-plugin` в `pom.xml` — подставляет полный `-classpath` с Guava и остальными зависимостями).

## Соответствие ClassTransformExample

| Идея | ClassTransform | Mixin (этот проект) |
|------|----------------|-------------------|
| Инъекции в конструктор / метод | `@CInject` | `@Inject` + `CallbackInfo` / `CallbackInfoReturnable` |
| Редирект вызова | `@CRedirect` + `INVOKE` | `@Redirect` + `@At(INVOKE)` |
| Редирект записи в поле | `@CRedirect` + `PUTFIELD` | `@Redirect` + `@At(FIELD, opcode = PUTFIELD)` |
| Тень поля / вызов оригинала | `@CShadow` | `@Shadow` (+ `@Mutable` при записи) |
| Полная замена метода | `@COverride` | `@Overwrite` |
| Подмена ldc-константы | `@CModifyConstant` | `@ModifyConstant` + `@Constant` |

### Два частых правила (чтобы не ловить ошибки при apply)

1. **`CallbackInfo` только для `void`.** Если целевой метод что‑то возвращает (в т.ч. при `@Inject` на `HEAD` / `RETURN` и т.п.), в колбэке нужен **`CallbackInfoReturnable<T>`**, где `T` совпадает с типом возврата. Иначе Mixin падает с требованием `CallbackInfoReturnable`.

2. **`@Redirect` на `PUTFIELD` (инстанс‑поле).** Сигнатура обработчика — **`(ВладелецПоля, НовоеЗначение)V`**, например для поля `cap` в классе `Budget`: **`(Lio/github/michael1297/examples/putfield/Budget;I)V`**. Первый аргумент — объект, в чьё поле пишут; второй — значение после `PUTFIELD`. Обработчик с одним `int` невалиден.

Подробнее по аннотациям Mixin: [MIXIN_ANNOTATIONS_RU.md](./MIXIN_ANNOTATIONS_RU.md). Сравнение с ClassTransform: [COMPARISON_CT_VS_MIXIN_RU.md](./COMPARISON_CT_VS_MIXIN_RU.md).

## Ссылки

- Репозиторий Mixin: https://github.com/SpongePowered/Mixin  
- Wiki: https://github.com/SpongePowered/Mixin/wiki  
- `MixinAgent` (hot-swap): https://github.com/SpongePowered/Mixin/blob/master/src/agent/java/org/spongepowered/tools/agent/MixinAgent.java  
