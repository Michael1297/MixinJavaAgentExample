package io.github.michael1297.mixin;

import io.github.michael1297.examples.putfield.Budget;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Budget.class, remap = false)
public class BudgetMixin {

    @Shadow
    @Mutable
    private int cap;

    /**
     * Перехват {@code PUTFIELD}: сигнатура {@code (Budget, int)V} — владелец поля и новое значение (требование Mixin).
     */
    @Redirect(
            method = "setCap",
            at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lio/github/michael1297/examples/putfield/Budget;cap:I")
    )
    private void classtransformExample$redirectCapPut(final Budget self, final int newValue) {
        int bounded = Math.min(Math.max(newValue, 0), 500);
        this.cap = bounded;
    }
}
