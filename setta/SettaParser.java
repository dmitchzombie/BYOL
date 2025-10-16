package setta;

import java.util.ArrayList;
import java.util.List;
import static setta.SettaTokenType.*;

public class SettaParser {
  private static class ParseError extends RuntimeException {}

  private final List<SettaToken> tokens;
  private int current = 0;

  SettaParser(List<SettaToken> tokens) {
    this.tokens = tokens;
  }

  // declaration* EOF
  List<Stmt> program() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  // declaration -> funDecl | letDecl | statement ;
  private Stmt declaration() {
    try {
      if (match(DEF)) return funDecl();
      if (match(LET)) return letDecl();
      return statement();
    } catch (ParseError e) {
      synchronize();
      return null;
    }
  }

  // funDecl -> "def" IDENTIFIER "(" parameters? ")" "=" expression ;
  private Stmt funDecl() {
    SettaToken name = consume(IDENTIFIER, "Expect function name.");
    consume(LEFT_PAREN, "Expect '(' after function name.");
    List<SettaToken> parameters = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        parameters.add(consume(IDENTIFIER, "Expect parameter name."));
      } while (match(COMMA));
    }
    consume(RIGHT_PAREN, "Expect ')' after parameters.");
    consume(EQUAL, "Expect '=' after ')'.");
    Expr body = expression();
    consume(SEMICOLON, "Expect ';' after function declaration.");
    return new Stmt.Fun(name, parameters, body);
  }

  // letDecl -> "let" IDENTIFIER "=" expression ;
  private Stmt letDecl() {
    SettaToken name = consume(IDENTIFIER, "Expect variable name.");
    consume(EQUAL, "Expect '=' after name.");
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Let(name, value);
  }

  // statement -> printStmt ;
  private Stmt statement() {
    if (match(PRINT)) return printStmt();
    throw error(peek(), "Expect statement 'print.'");
    // No other statement kinds implemented yet. Return null for now.
    // return null;
  }

  // "print" expression ;
  private Stmt printStmt() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }

  // expression -> equality
  private Expr expression() {
    return equality();
  }

  // equality -> comparison ( "=" comparison )? ;
  private Expr equality() {
    Expr expr = comparison();
    while (match(EQUAL)) {
      SettaToken operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  // comparison -> subset ( "subseteq" subset)* ;
  private Expr comparison() {
    Expr expr = subset();
    while (match(SUBSETEQ)) {
      SettaToken operator = previous();
      Expr right = subset();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  // subset -> union ( "in" union )* ;
  private Expr subset() {
    Expr expr = union();
    while (match(IN)) {
      SettaToken operator = previous();
      Expr right = union();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  // union -> intersection ( "union" intersection )* ;
  private Expr union() {
    Expr expr = intersection();
    while (match(UNION)) {
      SettaToken operator = previous();
      Expr right = intersection();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  // difference ( "intersect" difference )* ;
  private Expr intersection() {
    Expr expr = difference();
    while (match(INTERSECT)) {
      SettaToken operator = previous();
      Expr right = difference();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  // difference -> term ( "-" term )* ;
  private Expr difference() {
    Expr expr = term();
    while (match(MINUS)) {
      SettaToken operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  // term -> factor ( ( "+" | "-" ) factor )* ;
  private Expr term() {
    Expr expr = factor();
    while (match(PLUS, MINUS)) {
      SettaToken operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  // factor -> unary ( ( "*" | "/" | "%" ) unary )* ;
  private Expr factor() {
    Expr expr = unary();
    while (match(STAR, SLASH, PERCENT)) {
      SettaToken operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  // unary -> ( "-" | "!" ) unary | call ;
  private Expr unary() {
    if (match(MINUS, BANG)) {
      SettaToken operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }
    return call();
  }

  // call -> primary ( "(" arguments? ")" )* ;
  private Expr call() {
    Expr expr = primary();
    while (true) {
      if (match(LEFT_PAREN)) {
        expr = finishCall(expr);
      } else break;
    }
    return expr;
  }

  // finishCall same as lox, has 255 arg limit
  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check(RIGHT_PAREN)) {
      do {
        if (arguments.size() >= 255) {
          error(peek(), "Can't have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match(COMMA));
    }
    SettaToken paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
    return new Expr.Call(callee, paren, arguments);
  }

  /*
  // parses either roster 
  private Expr primary() {
    if (match(NUMBER)) {
      Object lit = previous().literal;
      return new Expr.Literal(lit);
    }
    if (match(STRING)) {
      Object lit = previous().literal;
      return new Expr.Literal(lit);
    }
    if (match(TRUE)) return new Expr.Literal(true);
    if (match(FALSE)) return new Expr.Literal(false);
    if (match(IDENTIFIER)) return new Expr.Variable(previous());
    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }
    if (match(LEFT_BRACE)) {
      // parse set literal: { expr, expr, ... }
      List<Expr> elements = new ArrayList<>();
      if (!check(RIGHT_BRACE)) {
        do {
          elements.add(expression());
        } while (match(COMMA));
      }
      consume(RIGHT_BRACE, "Expect '}' after set literal.");
      return new Expr.SetLiteral(elements);
    }

    throw error(peek(), "Expect expression.");
  }
  */

  // lots
  private Expr primary() {
    if (match(NUMBER)) return new Expr.Literal(previous().literal);
    if (match(STRING)) return new Expr.Literal(previous().literal);
    if (match(TRUE)) return new Expr.Literal(true);
    if (match(FALSE)) return new Expr.Literal(false);

    // "|" expression "|" -> cardinality
    if (match(PIPE)) {
      Expr expr = expression();
      consume(PIPE, "Expect '|' after expression.");
      return new Expr.Cardinality(expr);
    }

    if (match(IDENTIFIER)) return new Expr.Variable(previous());

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    if (match(LEFT_BRACE)) return setLiteralOrComprehension();

    throw error(peek(), "Expect expression");
  }

  // helper for if an expression is roster or builder
private Expr setLiteralOrComprehension() {
  if (match(RIGHT_BRACE)) {
    return new Expr.SetLiteral(new ArrayList<>()); // empty set {}
  }

  Expr first = expression();

  // builder form: { expr | x in expr (, expr)? }
  if (match(PIPE)) {
    SettaToken variable = consume(IDENTIFIER, "Expect variable name after '|'.");
    consume(IN, "Expect 'in' after variable name.");
    Expr domain = expression();
    Expr condition = null;
    if (match(COMMA)) {
      condition = expression();
    }
    consume(RIGHT_BRACE, "Expect '}' after comprehension.");
    return new Expr.Comprehension(first, variable, domain, condition);
  }

  // roster form: { e1, e2, ... }
  List<Expr> elements = new ArrayList<>();
  elements.add(first);
  while (match(COMMA)) {
    elements.add(expression());
  }
  consume(RIGHT_BRACE, "Expect '}' after set literal.");
  return new Expr.SetLiteral(elements);
}

// from lox
private void synchronize() {
  advance();
  while (!isAtEnd()) {
    if (previous().type == SEMICOLON) return;
    switch (peek().type) {
      case DEF:
      case LET:
      case PRINT:
        return;
        default:
          break;
    }
    advance();
  }
}


  // ---------- Helper Methods ---------------------------
  private boolean match(SettaTokenType... types) {
    for (SettaTokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private boolean check(SettaTokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private SettaToken advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private SettaToken peek() {
    return tokens.get(current);
  }

  private SettaToken previous() {
    return tokens.get(current - 1);
  }

  private SettaToken consume(SettaTokenType type, String message) {
    if (check(type)) return advance();
    throw error(peek(), message);
  }

  private ParseError error(SettaToken token, String message) {
    Setta.error(token.line, message);
    return new ParseError();
  }

public static void main(String[] args) {
    List<SettaToken> tokens = List.of(
        // let A = {1, 2, 3, 4, 5};
        new SettaToken(LET, "let", null, 1),
        new SettaToken(IDENTIFIER, "A", null, 1),
        new SettaToken(EQUAL, "=", null, 1),
        new SettaToken(LEFT_BRACE, "{", null, 1),
        new SettaToken(NUMBER, "1", 1.0, 1),
        new SettaToken(COMMA, ",", null, 1),
        new SettaToken(NUMBER, "2", 2.0, 1),
        new SettaToken(COMMA, ",", null, 1),
        new SettaToken(NUMBER, "3", 3.0, 1),
        new SettaToken(COMMA, ",", null, 1),
        new SettaToken(NUMBER, "4", 4.0, 1),
        new SettaToken(COMMA, ",", null, 1),
        new SettaToken(NUMBER, "5", 5.0, 1),
        new SettaToken(RIGHT_BRACE, "}", null, 1),
        new SettaToken(SEMICOLON, ";", null, 1),

        // let Squares = { x * x | x in A };
        new SettaToken(LET, "let", null, 2),
        new SettaToken(IDENTIFIER, "Squares", null, 2),
        new SettaToken(EQUAL, "=", null, 2),
        new SettaToken(LEFT_BRACE, "{", null, 2),
        new SettaToken(IDENTIFIER, "x", null, 2),
        new SettaToken(STAR, "*", null, 2),
        new SettaToken(IDENTIFIER, "x", null, 2),
        new SettaToken(PIPE, "|", null, 2),
        new SettaToken(IDENTIFIER, "x", null, 2),
        new SettaToken(IN, "in", null, 2),
        new SettaToken(IDENTIFIER, "A", null, 2),
        new SettaToken(RIGHT_BRACE, "}", null, 2),
        new SettaToken(SEMICOLON, ";", null, 2),

        // let Evens = { x | x in A , x % 2 = 0 };
        new SettaToken(LET, "let", null, 3),
        new SettaToken(IDENTIFIER, "Evens", null, 3),
        new SettaToken(EQUAL, "=", null, 3),
        new SettaToken(LEFT_BRACE, "{", null, 3),
        new SettaToken(IDENTIFIER, "x", null, 3),
        new SettaToken(PIPE, "|", null, 3),
        new SettaToken(IDENTIFIER, "x", null, 3),
        new SettaToken(IN, "in", null, 3),
        new SettaToken(IDENTIFIER, "A", null, 3),
        new SettaToken(COMMA, ",", null, 3),
        new SettaToken(IDENTIFIER, "x", null, 3),
        new SettaToken(PERCENT, "%", null, 3),
        new SettaToken(NUMBER, "2", 2.0, 3),
        new SettaToken(EQUAL, "=", null, 3),
        new SettaToken(NUMBER, "0", 0.0, 3),
        new SettaToken(RIGHT_BRACE, "}", null, 3),
        new SettaToken(SEMICOLON, ";", null, 3),

        // print Squares;
        new SettaToken(PRINT, "print", null, 4),
        new SettaToken(IDENTIFIER, "Squares", null, 4),
        new SettaToken(SEMICOLON, ";", null, 4),

        // print Evens;
        new SettaToken(PRINT, "print", null, 5),
        new SettaToken(IDENTIFIER, "Evens", null, 5),
        new SettaToken(SEMICOLON, ";", null, 5),

        new SettaToken(EOF, "", null, 6)
    );

    SettaParser parser = new SettaParser(tokens);
    List<Stmt> stmts = parser.program();

    for (Stmt stmt : stmts) {
        System.out.println(stmt);
    }
}

}





