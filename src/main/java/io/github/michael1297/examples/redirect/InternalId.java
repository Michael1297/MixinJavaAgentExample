package io.github.michael1297.examples.redirect;

/** Статический «источник id» — вызов будет перехвачён {@code @CRedirect}. */
public final class InternalId {

    private InternalId() {
    }

    public static long next() {
        return 1L;
    }
}
