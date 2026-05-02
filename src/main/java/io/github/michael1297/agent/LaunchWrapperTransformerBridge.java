package io.github.michael1297.agent;

import net.minecraft.launchwrapper.IClassTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Пробрасывает цепочку {@link net.minecraft.launchwrapper.IClassTransformer} Mixin в
 * {@link ClassFileTransformer}. Список трансформеров задаётся после {@code inject()} —
 * до этого момента список пустой, но сам агент уже зарегистрирован, чтобы классы,
 * подтягиваемые при bootstrap Mixin, тоже проходили через pipeline.
 */
final class LaunchWrapperTransformerBridge implements ClassFileTransformer {

    private final AtomicReference<List<IClassTransformer>> transformers =
            new AtomicReference<>(Collections.emptyList());

    void setTransformers(final List<IClassTransformer> transformers) {
        this.transformers.set(new ArrayList<>(transformers));
    }

    @Override
    public byte[] transform(
            final ClassLoader loader,
            final String className,
            final Class<?> classBeingRedefined,
            final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer
    ) {
        if (className == null || classfileBuffer == null) {
            return null;
        }
        final String dotted = className.replace('/', '.');
        byte[] cur = classfileBuffer;
        for (final IClassTransformer t : this.transformers.get()) {
            final byte[] next = t.transform(dotted, dotted, cur);
            if (next != null) {
                cur = next;
            }
        }
        return cur;
    }
}
