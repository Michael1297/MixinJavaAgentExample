package io.github.michael1297.mixin;

import io.github.michael1297.examples.redirect.MessageService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MessageService.class, remap = false)
public class MessageServiceMixin {

    @Redirect(
            method = "nextMessage",
            at = @At(value = "INVOKE", target = "Lio/github/michael1297/examples/redirect/InternalId;next()J")
    )
    private static long classtransformExample$redirectNext() {
        return 424242L;
    }
}
