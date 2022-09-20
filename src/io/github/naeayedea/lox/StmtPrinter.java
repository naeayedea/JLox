package io.github.naeayedea.lox;

public class StmtPrinter implements Stmt.Visitor<Void> {

    private final ASTPrinter exprPrinter = new ASTPrinter();

    public void print(Stmt stmt) {
        stmt.accept(this);
    }

    private void printTabs(long num) {
        StringBuilder tabs = new StringBuilder();
        for (int i = 0; i < num; i++) {
            tabs.append('\t');
        }
        System.out.print(tabs);
    }

    private void printWithTabs(String content, long offset) {
        printTabs(indents + offset); System.out.print(content);
    }

    //global variable purely for printing n number of tabs
    long indents = 0;
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        indents++;
        printWithTabs("BLOCK\n", -1);
        for (Stmt innerStmt : stmt.statements) {
            print(innerStmt);
        }
        printWithTabs("BLOCK END\n", -1);
        indents--;
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        indents++;
        printWithTabs("CLASS: " + stmt.name.lexeme + " METHODS:\n", -1);
        stmt.methods.forEach(s -> {printTabs(indents); s.accept(this);});
        printWithTabs("END CLASS\n", -1);
        indents--;
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        printWithTabs("EXPRESSION: "+exprPrinter.print(stmt.expression), 0);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        printWithTabs("FUNCTION: " + stmt.name.lexeme + " params ( ", -1);
        stmt.params.forEach(p -> System.out.print(p.lexeme + " "));
        System.out.print(") METHODS:\n");
        indents++;
        stmt.body.forEach(s -> s.accept(this));
        indents--;
        printWithTabs("END FUNCTION\n", 0);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (stmt.elseBranch == null) {
            printWithTabs("IF: "+exprPrinter.print(stmt.condition) + " THEN "+ stmt.thenBranch.accept(this) + ")\n", 0);
        } else {
            printWithTabs("IF: "+exprPrinter.print(stmt.condition) + stmt.thenBranch.accept(this) + " ELSE " + stmt.elseBranch.accept(this) + "\n", 0);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        printWithTabs("PRINT: "+exprPrinter.print(stmt.expression) + "\n", 0);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        printWithTabs("RETURN statement: " +stmt.keyword.lexeme + " (" + exprPrinter.print(stmt.value) + ")\n", 0);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer != null) {
            printWithTabs("VARIABLE: " + stmt.name.lexeme+ " " +exprPrinter.print(stmt.initializer) + "\n", 0);
        } else {
            printWithTabs("VARIABLE: " + stmt.name.lexeme+ " uninitialized\n", 0);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        printWithTabs("WHILE LOOP: " + stmt.condition.accept(exprPrinter) + "\n", 0);
        indents++;
        stmt.body.accept(this);
        indents--;
        printWithTabs("END LOOP\n", 0);
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        printWithTabs("BREAK\n", 0);
        return null;
    }
}
