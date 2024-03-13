package compiler.Parser;

public class LogicalExpression extends Expression{

    public LogicalExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }
}
