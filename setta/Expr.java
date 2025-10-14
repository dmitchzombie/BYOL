package setta;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitBinaryExpr(Binary expr);
    R visitUnaryExpr(Unary expr);
    R visitLiteralExpr(Literal expr);
    R visitVariableExpr(Variable expr);
    R visitGroupingExpr(Grouping expr);
    R visitSetLiteralExpr(SetLiteral expr);
    R visitComprehensionExpr(Comprehension expr);
    R visitCardinalityExpr(Cardinality expr);
  }
  static class Binary extends Expr {
    Binary(Expr left, SettaToken operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    final Expr left;
    final SettaToken operator;
    final Expr right;
  }
  static class Unary extends Expr {
    Unary(SettaToken operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    final SettaToken operator;
    final Expr right;
  }
  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    final Object value;
  }
  static class Variable extends Expr {
    Variable(SettaToken name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    final SettaToken name;
  }
  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    final Expr expression;
  }
  static class SetLiteral extends Expr {
    SetLiteral(List<Expr> elements) {
      this.elements = elements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSetLiteralExpr(this);
    }

    final List<Expr> elements;
  }
  static class Comprehension extends Expr {
    Comprehension(Expr expr, SettaToken variable, Expr inSet, Expr condition) {
      this.expr = expr;
      this.variable = variable;
      this.inSet = inSet;
      this.condition = condition;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitComprehensionExpr(this);
    }

    final Expr expr;
    final SettaToken variable;
    final Expr inSet;
    final Expr condition;
  }
  static class Cardinality extends Expr {
    Cardinality(SettaToken name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCardinalityExpr(this);
    }

    final SettaToken name;
  }

  abstract <R> R accept(Visitor<R> visitor);
}
