package io.github.michael1297.examples.access;

/** Приватное поле {@code coins} — к нему обращаются из трансформера через {@code @CShadow}. */
public class Wallet {

    private int coins = 10;

    public int getCoins() {
        return coins;
    }
}
