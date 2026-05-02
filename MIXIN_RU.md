# Sponge Mixin и «агент» JVM: что возможно

Официальный репозиторий: [SpongePowered/Mixin](https://github.com/SpongePowered/Mixin).

## Можно ли использовать Mixin как `-javaagent`?

**Частично.** В репозитории есть класс [`org.spongepowered.tools.agent.MixinAgent`](https://github.com/SpongePowered/Mixin/blob/master/src/agent/java/org/spongepowered/tools/agent/MixinAgent.java) с методами **`premain`** и **`agentmain`**, которые регистрируют `Instrumentation` и трансформер для **повторного применения миксинов / hot-swap** (см. Javadoc в исходниках: «re-transforms a mixin's target classes if the mixin has been redefined»).

Это **не** полноценная замена сценарию «один fat-jar с `Premain-Class`, как у ClassTransform»: для **первичного** запуска подсистемы Mixin на JVM Sponge historically опирается на **хост-платформу**:

- **LegacyLauncher** (`net.minecraft.launchwrapper`) + **`MixinTweaker`**;
- либо **ModLauncher** (`cpw.mods.modlauncher`) и соответствующие SPI.

В поставляемом JAR `mixin` в `META-INF/services/org.spongepowered.asm.service.IMixinService` перечислены сервисы **LaunchWrapper** и **ModLauncher**; отдельного «чистого AppClassLoader»-сервиса нет, поэтому **холодный старт** миксинов на обычном `java -jar …` без одной из этих оболочек **не поддержан из коробки**.

### Этот пример (`MixinJavaAgentExample`)

Используется **тот же набор целевых классов**, что и в `ClassTransformExample`. **Основной** сценарий — **`-javaagent`** с `Premain-Class` **`io.github.michael1297.agent.MixinDemoPremain`** в проектном JAR:

1. В `premain` рефлексией вызывается приватный конструктор **`net.minecraft.launchwrapper.Launch`**, чтобы появился непустой **`Launch.classLoader`** — иначе `MixinServiceLaunchWrapper.isValid()` ложен и Mixin не поднимется (см. SPI `IMixinService` в JAR Mixin: только LaunchWrapper и ModLauncher).
2. Тем же кодом, что и для tweaker, вызывается **`MixinBootstrap`** (`start` → **`mixins.demo.json`** → `doInit` → `inject`) через **`io.github.michael1297.agent.MixinBootstrapHelper`** (рефлексия на `start`/`inject`: в 0.8.7 они **package-private**).
3. Цепочка **`IClassTransformer`**, которую Mixin регистрирует на **`LaunchClassLoader`**, копируется в **`Instrumentation.addTransformer`** (**`LaunchWrapperTransformerBridge`**), потому что **`io.github.michael1297.Main`** и демо-классы грузятся **обычным** class loader приложения, а не через `Launch.main`. Мост вешается **до** `inject()`, иначе целевые классы могут успеть загрузиться «сырыми» при поднятии Mixin.
4. Без глубокого стека `Launch.launch` подсистема остаётся в фазе **PREINIT**; после `inject()` вызывается рефлексия **`MixinEnvironment.gotoPhase`** для **INIT** и **DEFAULT** (в tweaker-режиме то же делает **`EnvironmentStateTweaker`**).

Конфиг по-прежнему **`mixins.demo.json`**, в манифесте **`MixinConfigs`**.

**Альтернатива без агента:** `run-mixin-launch.cmd` или вручную `net.minecraft.launchwrapper.Launch --tweakClass io.github.michael1297.launch.MixinDemoTweaker` — tweaker только делегирует в `MixinBootstrapHelper`.

Встроенный в Mixin **`org.spongepowered.tools.agent.MixinAgent`** по-прежнему про **hot-swap / retransform** после уже поднятой подсистемы; для «как ClassTransform» здесь используется **свой** premain, а не `MixinAgent.premain`.

## Сборка и запуск

```bash
mvn -q package
```

Зависимости копируются в `target/lib/`. Запуск с агентом (Windows):

```bat
java -javaagent:target\MixinJavaAgentExample-1.0-SNAPSHOT.jar -cp "target\MixinJavaAgentExample-1.0-SNAPSHOT.jar;target\lib\*" io.github.michael1297.Main
```

(удобнее: `run-mixin-demo.cmd` после `package`.)

Через Maven нужен уже собранный JAR с манифестом агента:

```bash
mvn -q package exec:exec
```

(`exec-maven-plugin` подставляет `-javaagent:${project.build.directory}/${project.build.finalName}.jar`, полный `-classpath` и `io.github.michael1297.Main`.)

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
