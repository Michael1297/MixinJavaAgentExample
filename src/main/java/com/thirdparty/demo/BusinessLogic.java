package com.thirdparty.demo;

/**
 * Имитация класса из чужого WAR: трансформер ссылается только на полное имя через {@code @CTransformer(name = ...)}.
 */
public class BusinessLogic {

    public String execute() {
        return "done";
    }
}
