package io.github.michael1297.mixin;

import io.github.michael1297.examples.shadowcall.Echo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Echo.class, remap = false)
public class EchoMixin {

    @Shadow
    private String core() {
        throw new AssertionError();
    }

    @Overwrite
    public String say() {
        return "<<" + this.core() + ">>";
    }
}
