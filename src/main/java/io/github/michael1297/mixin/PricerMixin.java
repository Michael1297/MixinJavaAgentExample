package io.github.michael1297.mixin;

import io.github.michael1297.examples.returnval.Pricer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Pricer.class, remap = false)
public class PricerMixin {

    @Inject(method = "price", at = @At("RETURN"), cancellable = true)
    private void classtransformExample$onReturn(final CallbackInfoReturnable<Integer> cir) {
        int original = cir.getReturnValue();
        cir.setReturnValue(original * 3);
    }
}
