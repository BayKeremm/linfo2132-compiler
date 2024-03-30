package compiler.Parser;

public interface NodeVisitor {
    void visitTermExpression(TermExpression op);
    void visitUnaryNegateExpression(UnaryExpression op);

    void visitUnaryMinusExpression(UnaryExpression op);
    void visitFactorExpression(FactorExpression op);
    void visitPrimaryExpression(PrimaryExpression op);
    void visitIndexOperation(IndexOp op);
    GenericType visitIdentifierExpression(IdentifierExpression op);
    void visitDotOperation(DotOperation op);
    void visitLogicalExpression(LogicalExpression op);
    void visitComparisonExpression(ComparisionExpression op);
    void visitEqualityCheckExpression(EqualityCheckExpression op);
    void visitParameter(Parameter p );
    void visitArrayInitializer(ArrayInitializer p );
    void visitParanExpression(ParanExpression p );


    void visitConstantVariable(ConstantVariable var);

    void visitStructDeclaration(StructDeclaration s);
}
