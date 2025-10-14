package setta;

import java.util.ArrayList;
import java.util.List;
import static setta.SettaTokenType.*;

public class SettaParser {
  private final List<SettaToken> tokens;
  private int current = 0;

  SettaParser(List<SettaToken> tokens) {
    this.tokens = tokens;
  }

  List<Stmt> program() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  private Stmt declaration() {
    if (match(DEF)) return funDecl();
    if (match(LET)) return letDecl();
    return statement();
  }

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

  private Stmt letDecl() {
    SettaToken name = consume(IDENTIFIER, "Expect variable name.");
    consume(EQUAL, "Expect '=' after name.");
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Let(name, value);
  }

  private Stmt statement() {
    if (match(PRINT)) return printStmt();
    // No other statement kinds implemented yet. Return null for now.
    return null;
  }

  private Stmt printStmt() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }

  private Expr expression() {
    return equality();
  }

  private Expr equality() {
    Expr expr = comparison();
    while (match(EQUAL)) {
      SettaToken operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr comparison() {
    Expr expr = subset();
    while (match(SUBSETEQ)) {
      SettaToken operator = previous();
      Expr right = subset();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr subset() {
    Expr expr = union();
    while (match(IN)) {
      SettaToken operator = previous();
      Expr right = union();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr union() {
    Expr expr = intersection();
    while (match(UNION)) {
      SettaToken operator = previous();
      Expr right = intersection();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr intersection() {
    Expr expr = primary();
    while (match(INTERSECT)) {
      SettaToken operator = previous();
      Expr right = primary();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

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

  private static class ParseError extends RuntimeException {}

}



