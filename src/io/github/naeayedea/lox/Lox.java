package io.github.naeayedea.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    private static final boolean debug = true;

    private static boolean REPL;
    private static final Interpreter interpreter = new Interpreter();
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            REPL = true;
            runFile(args[0]);
        } else {
            REPL = false;
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
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

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
//        if (debug) {
//            System.out.println("\nScanned Tokens: ");
//            for (Token token : tokens) {
//                System.out.println(token);
//            }
//        }

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

    static void error(int line, String message) {
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
