package compiler.Parser;

public interface NodeVisitor {
    void visitTermExpression(TermExpression op);
    void visitUnaryNegateExpression(UnaryExpression op);

    void visitUnaryMinusExpression(UnaryExpression op);
    void visitFactorExpression(FactorExpression op);
    void visitPrimaryExpression(PrimaryExpression op);
    void visitIndexOperation(IndexOp op);
    void visitFunctionCallExpression(FunctionCallExpression exp);
    GenericType visitSymbolTableIdentifier(IdentifierExpression op);

    GenericType visitSymbolTableFunction(FunctionCallExpression op);

    GenericType visitSymbolTableIndexOp(IndexOp op);

    GenericType visitSymbolTableDotOp(DotOperation op);
    void visitProcedure(Procedure p);
    void visitWhile(WhileStatement w);
    void visitDotOperation(DotOperation op);
    void visitLogicalExpression(LogicalExpression op);
    void visitComparisonExpression(ComparisionExpression op);
    void visitEqualityCheckExpression(EqualityCheckExpression op);
    void visitParameter(Parameter p );
    void visitArrayInitializer(ArrayInitializer p );
    void visitParanExpression(ParanExpression p );


    void visitConstantVariable(VariableGod var);
    void visitGlobalVariable(VariableGod var);
    void visitScopeVariable(ScopeVariable var);

    void visitStructDeclaration(StructDeclaration s);
}
