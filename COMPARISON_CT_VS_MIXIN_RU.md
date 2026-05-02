# ClassTransform vs Sponge Mixin (эти два примера)

Оба проекта трансформируют **один и тот же** набор демонстрационных классов (`Greeter`, `BusinessLogic`, пакет `examples.*`, …) и выводят одинаковую логику в консоль (префиксы `[ClassTransform]` и `[Mixin]` различаются только меткой).

## Архитектура запуска

| | **ClassTransformExample** | **MixinJavaAgentExample** |
|--|---------------------------|----------------------------|
| Точка входа трансформации | `javaagent` + `TransformerManager.hookInstrumentation` | `Launch` + tweaker + `MixinBootstrap` |
| Зависимость от Minecraft API | Нет | Только **LaunchWrapper** как загрузчик/твикер (исторически из экосистемы Minecraft) |
| Типичный `java -jar` без доп. аргументов | Да (fat-jar с `Premain-Class`) | Нет — нужен `Launch` и `--tweakClass` |
| Встроенный `MixinAgent.premain` | — | Вспомогательный сценарий hot-swap, не дублируется в этом примере |

## Сопоставление API

| Задача | ClassTransform | Mixin |
|--------|----------------|-------|
| Вставка в начало / конец / return | `@CInject` + `@CTarget` | `@Inject` + `@At` |
| Отмена / смена return | `InjectionCallback` + `cancellable` | `CallbackInfoReturnable` + `cancellable` |
| Подмена вызова | `@CRedirect` | `@Redirect` |
| Подмена записи в поле | `@CRedirect` + `PUTFIELD` | `@Redirect` + `FIELD` + `PUTFIELD` |
| Доступ к оригинальному члену | `@CShadow` | `@Shadow` |
| Полная замена метода | `@COverride` | `@Overwrite` |
| Константа в методе | `@CModifyConstant` | `@ModifyConstant` |
| Цель по строке имени класса | `@CTransformer(name=…)` | Обычно всё равно нужен класс на classpath компиляции миксина; у нас `BusinessLogic` тот же, что в CT-проекте |

## Когда что выбирать

- **ClassTransform** — меньше инфраструктуры, удобен как **один javaagent** к любому процессу (в т.ч. Tomcat), без LegacyLauncher.
- **Mixin** — экосистема и инструменты вокруг Minecraft/ModLauncher; для **чистого** серверного Java без модлоадера обычно тяжелее; зато единый стиль с модами и богатая документация по `@Inject`/`@Redirect`.
