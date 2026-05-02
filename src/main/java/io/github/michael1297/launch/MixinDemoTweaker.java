package io.github.michael1297.launch;

import io.github.michael1297.agent.MixinBootstrapHelper;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

/**
 * Tweaker для LegacyLauncher: вызывает {@link org.spongepowered.asm.launch.MixinBootstrap} через
 * {@link io.github.michael1297.agent.MixinBootstrapHelper} (рефлексия — см. README).
 */
public final class MixinDemoTweaker implements ITweaker {

    public MixinDemoTweaker() {
        MixinBootstrapHelper.startAndAddDemoConfig();
    }

    @Override
    public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile) {
        MixinBootstrapHelper.doInit(args);
    }

    @Override
    public void injectIntoClassLoader(final LaunchClassLoader classLoader) {
        MixinBootstrapHelper.inject();
    }

    @Override
    public String getLaunchTarget() {
        return "io.github.michael1297.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
