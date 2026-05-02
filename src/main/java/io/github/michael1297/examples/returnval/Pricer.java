package io.github.michael1297.examples.returnval;

/** Возвращает фиксированную цену — трансформер умножит её через {@code InjectionCallback} на {@code RETURN}. */
public class Pricer {

    public int price() {
        return 100;
    }
}
