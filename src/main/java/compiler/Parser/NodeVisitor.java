package compiler.Parser;

import compiler.Parser.PlusOperation;

public interface NodeVisitor {
    public void visitPlusExp(PlusOperation op);
}
