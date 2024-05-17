package compiler.Parser;

/** UNARY EXPRESSION:
 *              unaryExpression -> ( NEGATE | MINUS ) unaryExpression | factorExpression
 * */
public abstract class UnaryExpression extends Expression {
    public UnaryExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }
    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public GenericType getType() {
        assert lhs == null;
        return rhs.getType();
    }

}
