package compiler.Parser.statements;

import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class BreakStatement extends Statement{

    public BreakStatement(int line) {
        super(line);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public void prettyPrint(String indentation) {

    }

    @Override
    public void codeGen(ByteVisitor b) {
        // TODO:
        b.visitBreak(this);
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        // Nothing to do here;

    }
}
