package compiler.Parser;

import compiler.semantics.TypeVisitor;

/** TERM EXPRESSION:
 *              termExpression -> unaryExpression ( ( MINUS | PLUS ) termExpression  )*
 * */
public abstract class TermExpression extends Expression{
    public TermExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }
    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }



    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitTermExpression(this);

    }
}
