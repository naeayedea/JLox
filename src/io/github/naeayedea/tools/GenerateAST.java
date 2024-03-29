package io.github.naeayedea.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAST(outputDir, "Expr", Arrays.asList(
                "Assign    : Token name, Expr value",
                "Binary    : Expr left, Token operator, Expr right",
                "Call      : Expr callee, Token paren, List<Expr> arguments",
                "Get       : Expr object, Token name",
                "Set       : Expr object, Token name, Expr value",
                "This      : Token keyword",
                "Grouping  : Expr expression",
                "Literal   : Object value",
                "Logical   : Expr left, Token operator, Expr right",
                "Unary     : Token operator, Expr right",
                "Variable  : Token name"
        ));

        defineAST(outputDir, "Stmt", Arrays.asList(
                "Block      : List<Stmt> statements",
                "Class      : Token name, List<Stmt.Function> methods",
                "Expression : Expr expression",
                "Function   : Token name, List<Token> params, List<Stmt> body, Boolean isStatic",
                "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Print      : Expr expression",
                "Return     : Token keyword, Expr value",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body",
                "Break      : Token keyword"
        ));
    }

    private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package io.github.naeayedea.lox.Parser;\n");
        writer.println("import java.util.List;\n");
        writer.println("import io.github.naeayedea.lox.Lexer.Token;\n");
                writer.println("public abstract class " + baseName + " {\n");
        defineVisitor(writer, baseName, types);
        //The AST classes
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
        //the base accept method
        writer.println("    public abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    public interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        //create new inner class
        writer.println("\n    public static class " + className + " extends " +baseName  + " {");
        //add fields
        String[] fields;
        if (fieldList.isEmpty()) {
            fields = new String[0];
        } else {
            fields = fieldList.split(", ");
        }
        for (String field : fields) {
            writer.println("        public final " + field + ";");
        }
        //create constructor
        writer.println("\n        public " + className + "(" + fieldList + ") {");
        //store parameters in fields
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }
        writer.println("        }");

        //visitor pattern
        writer.println();
        writer.println("        @Override");
        writer.println("        public <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");


        writer.println("   }");

    }
}
