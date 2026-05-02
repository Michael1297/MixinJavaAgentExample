# Mixin: аннотации в этом примере

Краткая карта соответствия классов в пакете `io.github.michael1297.mixin` и целей в `io.github.michael1297.*` / `com.thirdparty.demo`. Полная документация: [Wiki Mixin](https://github.com/SpongePowered/Mixin/wiki), [Javadoc](https://jenkins.liteloader.com/view/Other/job/Mixin/javadoc/index.html).

Все миксины объявлены с **`remap = false`**, т.к. имена не из SRG/MCP — как в зеркальном ClassTransform-проекте без обфускации.

---

## `@Mixin`

Указывает целевой класс (`value = …`). Один класс миксина на одну цель (как один `@CTransformer` на класс).

---

## `@Inject`

Вставка в точку метода. Параметр **`CallbackInfo`** для `void`, **`CallbackInfoReturnable<T>`** для методов с возвратом.

| Пример в проекте | Цель | Точка |
|------------------|------|--------|
| `GreeterMixin` | `<init>`, `greet` | `RETURN`, `HEAD` |
| `BusinessLogicMixin` | `execute` | `HEAD` |
| `OrderServiceMixin` | `process` | `RETURN` |
| `PricerMixin` | `price` | `RETURN`, `cancellable = true` — умножение возврата |

---

## `@Redirect`

Подмена вызова (`INVOKE`) или доступа к полю (`FIELD` + `opcode`).

| Пример | Описание |
|--------|----------|
| `MessageServiceMixin` | Статический редирект на `InternalId.next()J` |
| `BudgetMixin` | `PUTFIELD` в `cap` внутри `setCap` |

---

## `@Shadow` / `@Mutable`

Доступ к полю/методу цели с тем же именем (и при необходимости дескриптору). `@Mutable` разрешает присваивание полю-тени.

| Пример | Назначение |
|--------|------------|
| `WalletMixin` | Запись в `coins` перед `getCoins` |
| `BudgetMixin` | Запись ограниченного значения в `cap` |
| `EchoMixin` | Тень приватного `core()` для вызова из `@Overwrite` |

---

## `@Overwrite`

Полная замена тела метода цели (как `@COverride` в ClassTransform).

| Пример | Метод |
|--------|--------|
| `MathOpsMixin` | `sum` |
| `EchoMixin` | `say` |

---

## `@ModifyConstant`

Подмена константы в байткоде метода.

| Пример | Селектор |
|--------|----------|
| `BannerMixin` | `@Constant(stringValue = "demo")` на `label()` |

---

## Конфигурация

Файл **`src/main/resources/mixins.demo.json`**: список классов миксинов, `package`, `compatibilityLevel`, `minVersion`.

В `pom.xml` у **`maven-jar-plugin`** задано **`MixinConfigs: mixins.demo.json`**, чтобы контейнер Mixin подхватил конфиг из JAR при запуске через LaunchWrapper.
