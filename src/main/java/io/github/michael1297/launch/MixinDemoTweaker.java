package io.github.michael1297.launch;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigSource;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Tweaker для LegacyLauncher: вызывает {@link MixinBootstrap} через рефлексию, потому что
 * {@code start}/{@code doInit}/{@code inject} объявлены package-private, а размещение нашего класса
 * в {@code org.spongepowered.asm.launch} ломает подпись JAR-файла Mixin ({@code SecurityException}).
 *
 * <p>Конфиг регистрируется через {@link Mixins#addConfiguration(String, IMixinConfigSource)} с {@code null}
 * в качестве источника: одноаргументный {@link Mixins#addConfiguration(String)} внутри Mixin передаёт в
 * {@code Config.create} {@code fallbackEnvironment = null}, из‑за чего {@code MixinConfig.onLoad} падает с NPE.
 * После {@code start()} вызывается двухаргументная перегрузка (она подставляет {@code MixinEnvironment.getDefaultEnvironment()}).</p>
 */
public final class MixinDemoTweaker implements ITweaker {

    public MixinDemoTweaker() {
        invokeStaticNoArgs("start");
        Mixins.addConfiguration("mixins.demo.json", (IMixinConfigSource) null);
    }

    @Override
    public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile) {
        try {
            final Method doInit = MixinBootstrap.class.getDeclaredMethod("doInit", CommandLineOptions.class);
            doInit.setAccessible(true);
            doInit.invoke(null, CommandLineOptions.ofArgs(args));
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void injectIntoClassLoader(final LaunchClassLoader classLoader) {
        invokeStaticNoArgs("inject");
    }

    @Override
    public String getLaunchTarget() {
        return "io.github.michael1297.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
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
