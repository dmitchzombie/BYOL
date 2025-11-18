package setta;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Interpreter implements Expr.Visitor<Object> , Stmt.Visitor<Void> {
    private Environment environment = new Environment();

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
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case PLUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left + (double)right; //we don't have to worry about concatenating strings for our language
            case PERCENT: //for determining even / odd 
                checkNumberOperands(expr.operator, left, right);
                return (double)left % (double)right;
                //instead of saying "...| x is even" we say "...| x % 2 == 0"
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
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
            default:
                break;

    }
        return null;
  }
    

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
       Object right = evaluate(expr.right);

       switch (expr.operator.type) {
        case MINUS:
            checkNumberOperand(expr.operator, right);
            return -(double)right;
        case BANG:
            return !isTrue(right);
        default:
            return null;
       }
    }
    
    

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Object visitSetLiteralExpr(Expr.SetLiteral expr) {
        Set<Object> result = new LinkedHashSet<>();

        for (Expr elementExpr : expr.elements) {
            Object value = evaluate(elementExpr);
            result.add(value);
        }
        return result;
    }

    @Override
    public Object visitComprehensionExpr(Expr.Comprehension expr) {
        Object inSetValue = evaluate(expr.inSet);
        if (!(inSetValue instanceof Set<?>)) {
            throw new RuntimeError(expr.variable, "Right operand of 'in' must be a set.");
        }

        Set<Object> result = new LinkedHashSet<>();
        for (Object item : (Set<?>) inSetValue) {
            environment.define(expr.variable.lexeme, item);

            if (expr.condition != null) {
                Object conditionValue = evaluate(expr.condition);
                if (!isTrue(conditionValue)) {
                    continue;
                }
            }

            Object exprValue = evaluate(expr.expr);
            result.add(exprValue);
        }
        return result;
    }

    @Override
    public Object visitCardinalityExpr(Expr.Cardinality expr) {
        Object value = evaluate(expr.expression);
        if (!(value instanceof Set<?>))
            throw new RuntimeError(null, "Operand must be a set for cardinality.");
        return (double) ((Set<?>) value).size();
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if(!(callee instanceof SettaCallable)){
            throw new RuntimeError (expr.paren, "Can only call functions and classes.");
        }

        SettaCallable function = (SettaCallable)callee;
        return function.call(this, arguments);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
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
    Set<Object> result = new LinkedHashSet<>((Set<?>) left);
    result.addAll((Set<?>) right);
    return result;
}

private Object intersectValues(Object left, Object right) {
    checkSetOperands(left, right);
    Set<Object> result = new LinkedHashSet<>((Set<?>) left);
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

    // from book
    private void checkNumberOperand(SettaToken operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(SettaToken operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

//#endregion


//#region VISITOR METHODS FOR STMT 
    // from book
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
      //  System.out.println(value);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitLetStmt(Stmt.Let stmt) {
        Object value = evaluate(stmt.value);
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitFunStmt(Stmt.Fun stmt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
//#endregion

}

