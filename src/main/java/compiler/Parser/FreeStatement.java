package compiler.Parser;

import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class FreeStatement extends Statement{

    Expression identifierExp;
    GenericType type;
    protected FreeStatement(int line, Expression id) {
        super(line);
        this.identifierExp = id;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.println(indentation+"FreeStatement: " +identifierExp.toString());

    }

    @Override
    public GenericType getType() {
        return type;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        this.type = v.visitSymbolTableIdentifier((IdentifierExpression) identifierExp);
        v.visitFreeStatement(this);
    }

    @Override
    public void codeGen(ByteVisitor b) {

    }
}
