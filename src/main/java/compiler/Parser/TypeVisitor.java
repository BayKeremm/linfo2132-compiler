package compiler.Parser;

public class TypeVisitor implements NodeVisitor{
    @Override
    public void visitPlusExp(PlusOperation op) {
        System.out.println("Visiting Plus operation in typevisitor");
        System.out.println(op.lhs.getRep());
        System.out.println(op.rhs.getRep());
    }
}
