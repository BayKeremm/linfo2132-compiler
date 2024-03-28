package compiler.Parser;

public class TypeChecker implements NodeVisitor{
    Program program;
    public TypeChecker(Program p ){
        this.program = p;
    }
    @Override
    public void visitTermExpression(TermExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        System.out.println(lhs.getType() + " and " + rhs.getType());

    }
    public void visitUnaryExpression(UnaryExpression op) {
    }

    @Override
    public void visitFactorExpression(FactorExpression op) {

    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression op) {

    }

    @Override
    public void visitIndexOperation(IndexOp op) {

    }

    @Override
    public void visitIdentifierExpression(IdentifierExpression op) {

    }

    @Override
    public void visitDotOperation(DotOperation op) {

    }

    @Override
    public void visitLogicalExpression(LogicalExpression op) {

    }

    @Override
    public void visitComparisonExpression(ComparisionExpression op) {

    }

    @Override
    public void visitEqualityCheckExpression(EqualityCheckExpression op) {

    }

    @Override
    public void visitParameter(Parameter p) {

    }

    @Override
    public void visitArrayInitializer(ArrayInitializer p) {

    }

    public void typeCheck(){
        for(ConstantVariable c : program.constantVariables){
            c.declarator.typeAnalyse(this);
        }
    }

}
