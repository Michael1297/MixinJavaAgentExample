package io.github.michael1297.mixin;

import io.github.michael1297.demo.Greeter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Greeter.class, remap = false)
public class GreeterMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void classtransformExample$onConstructed(final CallbackInfo ci) {
        System.out.println("[Mixin] Greeter: constructor RETURN");
    }

    /**
     * Для метода с ненулевым возвратом на точке {@code HEAD} Mixin ожидает {@link CallbackInfoReturnable}, а не {@link CallbackInfo}.
     */
    @Inject(method = "greet", at = @At("HEAD"))
    private void classtransformExample$onGreetHead(final CallbackInfoReturnable<String> cir) {
        System.out.println("[Mixin] Greeter.greet: HEAD");
    }
}
