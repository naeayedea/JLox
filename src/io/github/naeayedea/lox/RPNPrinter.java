package io.github.naeayedea.lox;

public class RPNPrinter implements Expr.Visitor<String> {
    public static void main(String[] args) {

        Expr expression = new Expr.Binary(
        new Expr.Binary(new Expr.Literal(1), new Token(TokenType.PLUS, "+", null, 1), new Expr.Literal(2)),
        new Token(TokenType.STAR, "*", null, 1),
        new Expr.Binary(new Expr.Literal(4), new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(3)));

        System.out.println(new RPNPrinter().print(expression));
    }

    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return "(" + expr.name + " " + expr.value.accept(this) + " assignment)";
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return polandise(expr.operator.lexeme, expr.left, expr. right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return polandise("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value == null ? "nil" : expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return polandise(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return polandise(expr.operator.lexeme, expr. right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return "(" + expr.name + " variable)";
    }

    private String polandise(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (Expr expr : exprs) {
            builder.append(expr.accept(this));
            builder.append(" ");
        }
        builder.append(name).append(")");
        return builder.toString();
    }
}
