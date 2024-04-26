package compiler.Parser.expressions;

import compiler.bytecodegen.ByteVisitor;

public class MultiplyOperation extends FactorExpression {
    public MultiplyOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "*");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Multiply op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        MultiplyOperation a = (MultiplyOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitMul(this);
    }
}
