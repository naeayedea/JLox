package io.github.naeayedea.tools;


import io.github.naeayedea.lox.Parser.Expr;
import io.github.naeayedea.lox.Lexer.Token;
import io.github.naeayedea.lox.Lexer.TokenType;

import java.util.stream.Collectors;

public class ASTPrinter implements Expr.Visitor<String> {

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                new Expr.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expr.Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Literal(45.67)));

        System.out.println(new ASTPrinter().print(expression));
    }

    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return "(assignment "+ expr.name.lexeme + " " + expr.value.accept(this) + ")";
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        return "(function call: "+expr.callee.accept(this) + " parameters: (" + expr.arguments.stream().map(arg -> arg.accept(this)).collect(Collectors.joining(", "))+ "))";
    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        return "(field get: " + expr.name.lexeme + ")";
    }

    @Override
    public String visitSetExpr(Expr.Set expr) {
        return "(field set: " + expr.name.lexeme + " value: " + expr.value.accept(this) + ")";
    }

    @Override
    public String visitThisExpr(Expr.This expr) {
        return "this";
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value == null ? "nil" : expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return "(variable " + expr.name.lexeme + ")";
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
}
