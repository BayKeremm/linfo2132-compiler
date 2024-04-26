package compiler.Parser;

import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class UnaryMinusOperation extends UnaryExpression{
    public UnaryMinusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "-");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Unary minus op:\n");
        super.prettyPrint(indentation+" ");
    }

    @Override
    public boolean equals(Object o) {
        UnaryMinusOperation a = (UnaryMinusOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitUnaryMinusExpression(this);

    }

    @Override
    public void codeGen(ByteVisitor b) {

    }
}
