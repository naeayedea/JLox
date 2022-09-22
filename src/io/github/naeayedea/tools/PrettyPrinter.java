package io.github.naeayedea.tools;

import io.github.naeayedea.lox.Parser.Expr;
import io.github.naeayedea.lox.Parser.Stmt;
import io.github.naeayedea.lox.utils.Utilities;

import java.util.List;
import java.util.stream.Collectors;

public class PrettyPrinter implements Stmt.Visitor<Void>, Expr.Visitor<String> {

    StringBuilder file = new StringBuilder();

    public void print(List<Stmt> stmts) {
        for (Stmt stmt : stmts) {
            stmt.accept(this);
        }
        System.out.println(file);
    }

    //global variable purely for printing n number of tabs
    private long indents = 0;
    private long suppressTabs = 0;
    private boolean newLine;

    private void addWithTabs(String content, long offset) {
        StringBuilder tabs = new StringBuilder();
        if (suppressTabs <= 0) {
            for (int i = 0; i < indents + offset; i++) {
                tabs.append('\t');
            }
        }
        if (suppressTabs > 0) {
            suppressTabs--;
        }
        file.append(tabs.append(content));
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        addWithTabs("{\n", 0);
        indents++;
        for (Stmt innerStmt : stmt.statements) {
            innerStmt.accept(this);
        }
        indents--;
        addWithTabs("}", 0);
        if (!newLine) {
            newLine = true;
        } else {
            file.append('\n');
        }
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        indents++;
        addWithTabs(makeOrange("class ") + stmt.name.lexeme + " {\n", -1);
        stmt.methods.forEach(s -> s.accept(this));
        indents--;
        addWithTabs("}\n\n", 0);
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        addWithTabs(stmt.expression.accept(this) + ";\n", 0);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        if (stmt.isStatic) {
            addWithTabs(makeOrange("static ") + makeYellow(stmt.name.lexeme) + "(", 0);
        } else {
            addWithTabs(makeOrange("fun ") + makeYellow(stmt.name.lexeme) + "(", 0);
        }
        file.append(stmt.params.stream().map(p -> p.lexeme).collect(Collectors.joining(", ")));
        file.append(") {\n");
        indents++;
        stmt.body.forEach(s -> s.accept(this));
        indents--;
        addWithTabs("}\n\n", 0);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (stmt.elseBranch == null) {
            addWithTabs(makeOrange("if") + " (" + stmt.condition.accept(this) + ") ", 0);
            suppressTabs++;
            stmt.thenBranch.accept(this);
        } else {
            addWithTabs(makeOrange("if") + " (" + stmt.condition.accept(this) + ") ", 0);
            suppressTabs++; newLine = false;
            stmt.thenBranch.accept(this);
            file.append(makeOrange(" else "));
            suppressTabs++; newLine = false;
            stmt.elseBranch.accept(this);
        }

        if (!(stmt.elseBranch instanceof Stmt.If)) {
            file.append("\n");
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        addWithTabs(makeOrange("print "), 0);
        file.append(stmt.expression.accept(this)).append(";\n");
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (stmt.value == null) {
            addWithTabs(makeOrange("return;"), 0);
        } else {
            addWithTabs(makeOrange("return "), 0);
            file.append(stmt.value.accept(this));
            file.append(";\n");
        }
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer == null) {
            addWithTabs(makeOrange("var ") + stmt.name.lexeme + ";\n", 0);
        } else {
            addWithTabs(makeOrange("var ") + stmt.name.lexeme + " = " + stmt.initializer.accept(this) + ";\n", 0);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        addWithTabs(makeOrange("while") + " ("+stmt.condition.accept(this)+")", 0);
        stmt.body.accept(this);
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        file.append(makeOrange("break;\n"));
        return null;
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return expr.name.lexeme + "  = " + expr.value.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return expr.left.accept(this) + " " + expr.operator.lexeme + " " + expr.right.accept(this);

    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        return expr.callee.accept(this) + "(" + expr.arguments.stream().map(e -> e.accept(this)).collect(Collectors.joining(", ")) + ")";

    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        return expr.object.accept(this) + "." + expr.name.lexeme;

    }

    @Override
    public String visitSetExpr(Expr.Set expr) {
        return expr.object.accept(this) + "." + expr.name.lexeme + " = "+expr.value.accept(this);
    }

    @Override
    public String visitThisExpr(Expr.This expr) {
        return makeOrange("this");
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return "("+expr.expression.accept(this)+")";

    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value instanceof String) {
            return makeGreen("\"" + expr.value + "\"");
        }

        if (expr.value instanceof Boolean) {
            return makeOrange(expr.value.toString());
        }

        if (expr.value == null) {
            return makeOrange("nil");
        }
        return makeBlue(Utilities.stringify(expr.value));
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return expr.left.accept(this) + " " + expr.operator.lexeme + " " + expr.right.accept(this);

    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.operator.lexeme + expr.right.accept(this);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    private String makeRed(String message) {
        return colourString(message, "[31m");
    }

    private String makeGreen(String message) {
        return colourString(message, "[32m");
    }

    private String makeYellow(String message) {
        return colourString(message, "[93m");
    }

    private String makeBlue(String message) {
        return colourString(message, "[34m");
    }

    private String makeMagenta(String message) {
        return colourString(message, "[35m");
    }

    private String makeCyan(String message) {
        return colourString(message, "[36m");
    }

    private String makeWhite(String message) {
        return colourString(message, "[37m");
    }

    private String makeOrange(String message) {
        return colourString(message, "[33m");
    }

    private String colourString(String message, String ANSI) {
        return '\u001b' + ANSI + message + "\u001b[0m";
    }

}
