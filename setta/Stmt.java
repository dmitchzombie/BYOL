package setta;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitPrintStmt(Print stmt);
    R visitLetStmt(Let stmt);
    R visitFunStmt(Fun stmt);
    R visitExpressionStmt(Expression stmt);
  }
  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;

    @Override
    public String toString() {
      return "Print(" + expression + ")";
    }
  }
  static class Let extends Stmt {
    Let(SettaToken name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLetStmt(this);
    }

    final SettaToken name;
    final Expr value;

    @Override
    public String toString() {
      return "Let(" + name + ", " + value + ")";
    }
  }
  static class Fun extends Stmt {
    Fun(SettaToken name, List<SettaToken> params, Expr body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunStmt(this);
    }

    final SettaToken name;
    final List<SettaToken> params;
    final Expr body;

    @Override
    public String toString() {
      return "Fun(" + name + ", " + params + ", " + body + ")";
    }
  }
  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;

    @Override
    public String toString() {
      return "Expression(" + expression + ")";
    }
  }

  abstract <R> R accept(Visitor<R> visitor);
}
