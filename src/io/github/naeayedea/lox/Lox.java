package io.github.naeayedea.lox;

import io.github.naeayedea.lox.Interpreter.Interpreter;
import io.github.naeayedea.lox.Lexer.Scanner;
import io.github.naeayedea.lox.Lexer.Token;
import io.github.naeayedea.lox.Lexer.TokenType;
import io.github.naeayedea.lox.Parser.Parser;
import io.github.naeayedea.lox.Parser.Stmt;
import io.github.naeayedea.lox.analysis.Resolver;
import io.github.naeayedea.lox.errors.RuntimeError;
import io.github.naeayedea.tools.StmtPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    private static boolean debug = false;

    private static boolean REPL = false;
    private static final Interpreter interpreter = new Interpreter();
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            if (args[1].equals("-debug")) {
                debug = true;
                runFile(args[0]);
            } else {
                System.out.println("Usage: jlox [script] (-debug)?");
                System.exit(64);
            }
        } else if (args.length == 1) {
            if (args[0].equals("-debug")) {
                debug = true;
                runPrompt();
            } else {
                runFile(args[0]);
            }
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String file = new String(bytes, Charset.defaultCharset());
        System.out.println("--------------------------------- FILE START ---------------------------------");
        System.out.println(file);
        System.out.println("--------------------------------- FILE END ---------------------------------\n");
        System.out.println("RESULT: ");
        run(file);
        if (hadError) {
            System.exit(65);
        }
        if (hadRuntimeError) {
            System.exit(70);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        REPL = true;
        for(;;) {
            System.out.println("> ");
            String line = reader.readLine();
            if (line == null ) break;
            try {
                run(line);
            } catch (RuntimeException ignored) {}
            hadError = false;
        }
    }

    private static void run(String source) {
        //Scan file and separate into tokens
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        //Parse tokens into an executable list of statements
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        //stop if there's a syntax error
        if (hadError) return;

        //resolve variables
        if (!REPL) {
            Resolver resolver = new Resolver(interpreter);
            resolver.resolve(statements);
            resolver.reportErrors();
        }

        //stop if there's a resolution error
        if (hadError) return;

        //prints debug information if debug flag is set
        debug(tokens, statements);

        //if 'shell' is being used, evaluate a single expression and print, otherwise interpret code normally
        if (REPL && statements.size() == 1 && statements.get(0) instanceof Stmt.Expression) {
            System.out.println(interpreter.evaluate(((Stmt.Expression) statements.get(0)).expression));
        } else {
            interpreter.interpret(statements);
        }
    }

    private static void debug(List<Token> tokens, List<Stmt> statements) {

        if (debug) {
            System.out.println("\nParsed Statements: ");
            for (Stmt stmt : statements) {
                (new StmtPrinter()).print(stmt);
            }
        }

        if (debug) {
            System.out.println("\nExecution: ");
        }
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println("Runtime Error: "+error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
