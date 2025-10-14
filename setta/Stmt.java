package setta;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitPrintStmt(Print stmt);
    R visitLetStmt(Let stmt);
    R visitFunStmt(Fun stmt);
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
  }

  abstract <R> R accept(Visitor<R> visitor);
}
