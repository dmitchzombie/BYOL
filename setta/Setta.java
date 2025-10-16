package setta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Setta {

  static boolean hadError = false;

  public static void main(String[] args) throws IOException {
    runFile("setta/test1.setta");
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    String source = new String(bytes, Charset.defaultCharset());

    System.out.println("File contents:\n" + source);

    run(source);

    if (hadError)
      System.exit(65);

  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

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

    System.out.println("Parsed AST");
    for (Stmt stmt : statements) {
      System.out.println(stmt);
    }
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  private static void report(int line, String where,
      String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

}
