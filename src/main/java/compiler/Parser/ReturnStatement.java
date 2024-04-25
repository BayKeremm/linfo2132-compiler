package compiler.Parser;

import compiler.semantics.TypeVisitor;

public class ReturnStatement extends Statement {
    public Expression getReturnExpression() {
        return expression;
    }

    Expression expression;

    protected ReturnStatement(int line, Expression expression) {
        super(line);
        this.expression = expression;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Return statement:\n");
        expression.prettyPrint(indentation+"  ");

    }

    @Override
    public String toString() {
        return "ReturnStatement{ " +
                 expression +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        ReturnStatement r = (ReturnStatement) o;

        if(!this.expression.equals(r.expression)) return false;
        
        return true;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        // TODO: Return statement type analyse
        v.visitReturn(this);

    }
}
