package compiler.Parser;

import compiler.bytecodegen.ByteVisitor;

public class GTComparison extends ComparisionExpression {
    public GTComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ">");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"GT comp:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        GTComparison a = (GTComparison) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitGT(this);
    }
}
