package io.github.michael1297.mixin;

import io.github.michael1297.examples.access.Wallet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Wallet.class, remap = false)
public class WalletMixin {

    @Shadow
    @Mutable
    private int coins;

    @Inject(method = "getCoins", at = @At("HEAD"))
    private void classtransformExample$forceBalance(final CallbackInfoReturnable<Integer> cir) {
        this.coins = 999;
    }
}
