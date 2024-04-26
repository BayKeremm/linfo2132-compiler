package compiler.Parser.expressions;

import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class UnaryNegateOperation extends UnaryExpression{
    public UnaryNegateOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!");
    }



    @Override
    public boolean equals(Object o) {
        UnaryNegateOperation a = (UnaryNegateOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitUnaryNegateExpression(this);

    }
    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Unary Negate op:\n");
        super.prettyPrint(indentation+" ");
    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitUNegate(this);
    }
}
