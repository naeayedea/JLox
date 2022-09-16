package io.github.naeayedea.lox;

public class StmtPrinter implements Stmt.Visitor<Void> {

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
    //global variable purely for printing n number of tabs
    long recursions = 0;
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        recursions++;
        System.out.println("BLOCK");
        for (Stmt innerStmt : stmt.statements) {
            printTabs(recursions);
            print(innerStmt);
        }
        printTabs(recursions - 1);
        System.out.println("BLOCK END");
        recursions--;
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        System.out.println("EXPRESSION: "+(new ASTPrinter()).print(stmt.expression));
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (stmt.elseBranch == null) {
            System.out.println("IF: "+(new ASTPrinter()).print(stmt.condition) + " THEN "+ stmt.thenBranch.accept(this) + ")");
        } else {
            System.out.println("IF: "+(new ASTPrinter()).print(stmt.condition) + stmt.thenBranch.accept(this) + " ELSE " + stmt.elseBranch.accept(this));

        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        System.out.println("PRINT: "+(new ASTPrinter()).print(stmt.expression));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer != null) {
            System.out.println("VARIABLE: " + stmt.name+ " " +(new ASTPrinter()).print(stmt.initializer));
        } else {
            System.out.println("VARIABLE: " + stmt.name+ " uninitialized");
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        return null;
    }
}
