package io.github.naeayedea.lox.errors;

public class Return extends RuntimeException {
    public final Object value;

    public Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
