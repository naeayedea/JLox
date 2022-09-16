package io.github.naeayedea.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Object get(Token name) {
        Object value;
        if (values.containsKey(name.lexeme)) {
            value = values.get(name.lexeme);
        } else {
            if (enclosing != null) {
                value = enclosing.get(name);
            } else {
                throw new RuntimeError(name, "Undefined variable: '" + name.lexeme + "'.");
            }
        }

        if (value != null) return value;

        throw new RuntimeError(name, "Uninitialized variable: '" + name.lexeme + "'.");
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
        } else if (enclosing != null)  {
            enclosing.assign(name, value);
        } else {
            throw new RuntimeError(name, "Undefined variable: '" + name.lexeme + "'.");
        }
    }
}
