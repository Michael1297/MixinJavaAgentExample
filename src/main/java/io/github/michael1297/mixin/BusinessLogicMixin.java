package io.github.michael1297.mixin;

import com.thirdparty.demo.BusinessLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BusinessLogic.class, remap = false)
public class BusinessLogicMixin {

    @Inject(method = "execute", at = @At("HEAD"))
    private void classtransformExample$onExecuteHead(final CallbackInfoReturnable<String> cir) {
        System.out.println("[Mixin] BusinessLogic.execute: HEAD (аналог @CTransformer name=)");
    }
}
