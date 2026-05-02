package io.github.michael1297.examples.override;

/** Метод {@link #sum(int, int)} будет полностью заменён трансформером с {@code @COverride}. */
public class MathOps {

    public int sum(final int a, final int b) {
        return a + b;
    }
}
