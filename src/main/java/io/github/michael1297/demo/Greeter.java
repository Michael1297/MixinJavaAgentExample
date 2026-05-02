package io.github.michael1297.demo;

/** Простой класс-мишень для {@link io.github.michael1297.agent.GreeterTransformer}. */
public class Greeter {

    public Greeter() {
    }

    public String greet(final String name) {
        return "Hello, " + name;
    }
}
