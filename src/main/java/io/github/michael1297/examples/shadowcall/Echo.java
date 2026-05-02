package io.github.michael1297.examples.shadowcall;

/** Приватный {@link #core()} и публичный {@link #say()} — оригинал вызывается через {@code @CShadow native}. */
public class Echo {

    private String core() {
        return "core";
    }

    public String say() {
        return core();
    }
}
