package setta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Setta {
  private static Interpreter interpreter = new Interpreter();
  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException {
     runFile("setta/test2.setta");
    if (args.length > 1) {
      System.out.println("Usage: setta [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    interpreter = new Interpreter();
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    String source = new String(bytes, Charset.defaultCharset());
    run(source);

    if (hadError)
      System.exit(65);
    if (hadRuntimeError)
      System.exit(70);

  }

  private static void runPrompt() throws IOException {
    interpreter = new Interpreter();
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    System.out.println("Welcome to Setta!");

    for (;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null)
        break;
      run(line);

      hadError = false;
    }
  }

  private static void run(String source) {
    SettaScanner scanner = new SettaScanner(source);
    List<SettaToken> tokens = scanner.scanTokens();

    /*
     * // For now, just print the tokens.
     * for (SettaToken token : tokens) {
     * System.out.println(token);
     * }
     */

    // from book
    SettaParser parser = new SettaParser(tokens);
    List<Stmt> statements = parser.program();

    if (hadError)
      return;

    interpreter.interpret(statements);

    /*
     * uncomment to see AST
     * System.out.println("Parsed AST");
     * for (Stmt stmt : statements) {
     * System.out.println(stmt);
     * }
     */
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }

  private static void report(int line, String where,
      String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

  // for scanning
  // static void error(SettaToken token, String message) {
  //   if (token.type == SettaTokenType.EOF) {
  //     report(token.line, " at end", message);
  //   } else {
  //     report(token.line, " at '" + token.lexeme + "'", message);
  //   }
  // }

}
