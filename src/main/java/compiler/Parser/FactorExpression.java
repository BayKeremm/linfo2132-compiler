package compiler.Parser;

import compiler.semantics.TypeVisitor;

public abstract class FactorExpression extends Expression{
    public FactorExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitFactorExpression(this);

    }
}
