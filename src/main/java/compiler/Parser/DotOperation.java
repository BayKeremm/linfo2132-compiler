package compiler.Parser;

import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class DotOperation extends Expression{
    GenericType type;

    public DotOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ".");
    }



    @Override
    public String getRep() {
        return null;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        this.type = v.visitSymbolTableDotOp(this);
        v.visitDotOperation(this);
    }

    @Override
    public GenericType getType() {
        return type;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Dot op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        DotOperation a = (DotOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }

    @Override
    public void codeGen(ByteVisitor b) {

    }
}
