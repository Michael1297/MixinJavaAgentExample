package io.github.michael1297.examples.lifecycle;

/** Пустой метод — код «после оригинала» добавляется инъекцией в {@code RETURN}. */
public class OrderService {

    public void process() {
        System.out.println("[OrderService] work");
    }
}
