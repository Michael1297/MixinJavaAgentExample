package io.github.michael1297.examples.redirect;

/** Демонстрация {@code @CRedirect} на вызов {@link InternalId#next()}. */
public class MessageService {

    public String nextMessage() {
        long id = InternalId.next();
        return "msg-" + id;
    }
}
