package setta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Interpreter implements Expr.Visitor<Object> , Stmt.Visitor<Void> {

void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Setta.runtimeError(error);
        }
    }

    // Executes a single statement
    private void execute(Stmt stmt) {
        stmt.accept(this);
    }


private String stringify(Object object) {
    if (object == null) return "nil";

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }

//#region VISITOR METHODS FOR EXPR
  @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                return (double)left - (double)right;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double)right;
            case PLUS:
                return (double)left + (double)right; //we don't have to worry about concatenating strings for our language
            case PERCENT: //for determining even / odd 
                return (double)left % (double)right;
                //instead of saying "...| x is even" we say "...| x % 2 == 0"
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;

            case BANG_EQUAL: 
                return !isEqual(left, right);
            case EQUAL_EQUAL: 
                return isEqual(left, right);

            //These cases use helper methods to avoid repetitive code.
            case UNION:
                return unionValues(left,right);
            case INTERSECT:
                return intersectValues(left,right);
            case SUBSETEQ:
                return subseteqValues(left, right);
            case IN:
                return inValues(left, right);

    }
        return null;
  }
    

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
       Object right = evaluate(expr.right);

       switch (expr.operator.type) {
        case MINUS:
            return -(double)right;
        case BANG:
            return !isTrue(right);
       }
        return null;
    }
    
    

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitVariableExpr'");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitSetLiteralExpr(Expr.SetLiteral expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitSetLiteralExpr'");
    }

    @Override
    public Object visitComprehensionExpr(Expr.Comprehension expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitComprehensionExpr'");
    }

    @Override
    public Object visitCardinalityExpr(Expr.Cardinality expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCardinalityExpr'");
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCallExpr'");
    }
//#endregion 


//#region HELPER METHODS

private Object evaluate(Expr expr) {
        return expr.accept(this);
  }

private boolean isTrue(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        if (object instanceof Set && ((Set<?>) object).isEmpty()) return false;
        return true;
    }

private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }
    
private Object unionValues(Object left, Object right) {
    checkSetOperands(left, right);
    Set<Object> result = new HashSet<>((Set<?>) left);
    result.addAll((Set<?>) right);
    return result;
}

private Object intersectValues(Object left, Object right) {
    checkSetOperands(left, right);
    Set<Object> result = new HashSet<>((Set<?>) left);
    result.retainAll((Set<?>) right);
    return result;
}

private Object subseteqValues(Object left, Object right) {
    checkSetOperands(left, right);
    return ((Set<?>) right).containsAll((Set<?>) left);
}

private Object inValues(Object left, Object right) {
    if (!(right instanceof Set<?>))
        throw new RuntimeError(null, "Right operand must be a set for 'in'.");
    return ((Set<?>) right).contains(left);
}

private void checkSetOperands(Object left, Object right) {
    if (!(left instanceof Set<?>) || !(right instanceof Set<?>)) {
        throw new RuntimeError(null, "Operands must be sets.");
    }
}

//#endregion


//#region VISITOR METHODS FOR STMT 
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visitLetStmt(Stmt.Let stmt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Void visitFunStmt(Stmt.Fun stmt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
//#endregion

}

