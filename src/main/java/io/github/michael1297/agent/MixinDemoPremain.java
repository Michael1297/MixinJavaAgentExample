package io.github.michael1297.agent;

import net.minecraft.launchwrapper.Launch;

import org.spongepowered.asm.mixin.MixinEnvironment;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Запуск Mixin как {@code -javaagent} без точки входа {@link net.minecraft.launchwrapper.Launch}.
 *
 * <p>Mixin 0.8.7 выбирает только {@link org.spongepowered.asm.service.mojang.MixinServiceLaunchWrapper}
 * или ModLauncher; для валидности LaunchWrapper нужен непустой {@link Launch#classLoader}.
 * Рефлексия по приватному конструктору {@link Launch} создаёт {@link net.minecraft.launchwrapper.LaunchClassLoader} так же,
 * как при старте через {@code Launch.main}, но без парсинга аргументов и без загрузки main через LCL.</p>
 *
 * <p>Классы приложения при этом грузятся с {@link ClassLoader} приложения; цепочка трансформеров,
 * которую Mixin вешает на {@link net.minecraft.launchwrapper.LaunchClassLoader}, пробрасывается сюда через
 * {@link LaunchWrapperTransformerBridge} и {@link Instrumentation#addTransformer}.</p>
 *
 * <p>Без стека {@code Launch.launch} Mixin стартует в фазе {@link MixinEnvironment.Phase#PREINIT}; после
 * {@code inject()} вызывается {@link MixinBootstrapHelper#gotoPhase} для {@link MixinEnvironment.Phase#INIT}
 * и {@link MixinEnvironment.Phase#DEFAULT} (аналог {@code EnvironmentStateTweaker}). Трансформер моста
 * регистрируется в {@code Instrumentation} до {@code inject()}, чтобы классы, подгружаемые при bootstrap,
 * тоже проходили через цепочку.</p>
 */
public final class MixinDemoPremain {

    private static final AtomicBoolean INSTALLED = new AtomicBoolean(false);

    public static void premain(@SuppressWarnings("unused") final String agentArgs, final Instrumentation inst) {
        install(inst);
    }

    public static void agentmain(@SuppressWarnings("unused") final String agentArgs, final Instrumentation inst) {
        install(inst);
    }

    private static void install(final Instrumentation inst) {
        if (!INSTALLED.compareAndSet(false, true)) {
            return;
        }
        try {
            initLaunchStatics();
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException("Could not initialise Launch.classLoader for Mixin", e);
        }
        final LaunchWrapperTransformerBridge bridge = new LaunchWrapperTransformerBridge();
        inst.addTransformer(bridge, true);
        MixinBootstrapHelper.startAndAddDemoConfig();
        MixinBootstrapHelper.doInit(Collections.<String>emptyList());
        MixinBootstrapHelper.inject();
        MixinBootstrapHelper.gotoPhase(MixinEnvironment.Phase.INIT);
        MixinBootstrapHelper.gotoPhase(MixinEnvironment.Phase.DEFAULT);
        bridge.setTransformers(Launch.classLoader.getTransformers());
    }

    /**
     * Аналог первой фазы {@link Launch#Launch()}: создаёт {@link net.minecraft.launchwrapper.LaunchClassLoader} и кладёт его в
     * {@link Launch#classLoader}, чтобы сервис LaunchWrapper считался валидным.
     */
    private static void initLaunchStatics() throws ReflectiveOperationException {
        final Constructor<Launch> ctor = Launch.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
    }
}
