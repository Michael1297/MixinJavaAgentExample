package io.github.michael1297.examples.putfield;

/** Запись в {@code cap} перехватывается {@code @CRedirect} на {@code PUTFIELD}. */
public class Budget {

    private int cap;

    public void setCap(final int cap) {
        this.cap = cap;
    }

    public int getCap() {
        return cap;
    }
}
