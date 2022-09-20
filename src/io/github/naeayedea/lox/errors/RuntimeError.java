package io.github.naeayedea.lox.errors;

import io.github.naeayedea.lox.Lexer.Token;

public class RuntimeError extends RuntimeException {

    public final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
