package io.github.michael1297.mixin;

import io.github.michael1297.examples.lifecycle.OrderService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OrderService.class, remap = false)
public class OrderServiceMixin {

    @Inject(method = "process", at = @At("RETURN"))
    private void classtransformExample$afterProcess(final CallbackInfo ci) {
        System.out.println("[Mixin] OrderService.process: after original (RETURN)");
    }
}
