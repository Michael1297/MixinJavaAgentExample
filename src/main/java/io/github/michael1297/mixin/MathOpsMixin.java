package io.github.michael1297.mixin;

import io.github.michael1297.examples.override.MathOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = MathOps.class, remap = false)
public class MathOpsMixin {

    /**
     * Полная замена метода (аналог {@code @COverride}).
     */
    @Overwrite
    public int sum(final int a, final int b) {
        return (a + b) * 10;
    }
}
