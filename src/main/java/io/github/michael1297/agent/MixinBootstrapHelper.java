package io.github.michael1297.agent;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigSource;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Общий холодный старт Mixin: {@code start} → конфиг → {@code doInit} → {@code inject}.
 * Вызывается из tweaker (Launch) или из {@link MixinDemoPremain premain}.
 */
public final class MixinBootstrapHelper {

    private MixinBootstrapHelper() {
    }

    public static void startAndAddDemoConfig() {
        invokeStaticNoArgs("start");
        Mixins.addConfiguration("mixins.demo.json", (IMixinConfigSource) null);
    }

    public static void doInit(final List<String> args) {
        try {
            final Method doInit = MixinBootstrap.class.getDeclaredMethod("doInit", CommandLineOptions.class);
            doInit.setAccessible(true);
            doInit.invoke(null, CommandLineOptions.ofArgs(args));
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void inject() {
        invokeStaticNoArgs("inject");
    }

    /**
     * Без глубокого стека {@code Launch.launch} сервис LaunchWrapper отдаёт фазу {@link MixinEnvironment.Phase#PREINIT};
     * apply миксинов ожидает {@link MixinEnvironment.Phase#DEFAULT}. В tweaker-пути фазу переводит
     * {@code EnvironmentStateTweaker}; для {@code -javaagent} вызываем {@code gotoPhase} рефлексией.
     */
    public static void gotoPhase(final MixinEnvironment.Phase phase) {
        try {
            final Method m = MixinEnvironment.class.getDeclaredMethod("gotoPhase", MixinEnvironment.Phase.class);
            m.setAccessible(true);
            m.invoke(null, phase);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void invokeStaticNoArgs(final String name) {
        try {
            final Method m = MixinBootstrap.class.getDeclaredMethod(name);
            m.setAccessible(true);
            m.invoke(null);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
