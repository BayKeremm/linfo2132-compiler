package compiler.Parser;

import compiler.bytecodegen.ByteVisitor;

public class EqualComparison extends EqualityCheckExpression {

    public EqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "==");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Equality comp:\n");
        super.prettyPrint(indentation+" ");
    }

    @Override
    public boolean equals(Object o) {
        EqualComparison a = (EqualComparison) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitEquality(this);
    }
}
