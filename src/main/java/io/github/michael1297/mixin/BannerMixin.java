package io.github.michael1297.mixin;

import io.github.michael1297.examples.modifyconst.Banner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = Banner.class, remap = false)
public class BannerMixin {

    @ModifyConstant(method = "label", constant = @Constant(stringValue = "demo"))
    private String classtransformExample$patchLabel(final String original) {
        return original + "+patched";
    }
}
