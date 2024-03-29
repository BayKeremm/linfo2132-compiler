package compiler.Parser;

import compiler.Lexer.Token;
import static java.lang.System.exit;

public class TypeChecker implements NodeVisitor{
    Program program;
    public TypeChecker(Program p ){
        this.program = p;
    }
    private Boolean checkTypes(Expression lhs, Expression rhs){
        Type tl = lhs.getType();
        Type tr = rhs.getType();
        if(tl.type.equals(tr.type)){
            return true;
        } else if (tl.type.equals("<FLOAT_LITERAL>") && tr.type.equals("<NATURAL_LITERAL>")) {
            return true;
        } else if (tr.type.equals("<FLOAT_LITERAL>") && tl.type.equals("<NATURAL_LITERAL>")) {
            return true;
        }
        return false;
    }
    private Boolean checkStatementTypes(TypeDeclaration idType, Expression dec){
        String type = idType.type.token().image();
        Type dec_type = dec.getType();
        switch (type) {
            case "int", "float" -> {
                return dec_type.toString().equals(Token.NATURAL_LITERAL.image()) || dec_type.toString().equals(Token.FLOAT_LITERAL.image());
            }
            case "string" -> {
                return dec_type.toString().equals(Token.STRING_LITERAL.image());
            }
            case "bool" -> {
                return dec_type.toString().equals(Token.BOOLEAN_LITERAL.image());
            }
        }
        return false;
    }
    private void reportSemanticError(String message,int line, Object... args)  {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_RED = "\u001B[31m";
        System.out.print(ANSI_RED);
        System.err.printf("%s:%d: error: ", program.getFileName(), line);
        System.err.printf(message, args);
        System.err.println();
        System.out.print(ANSI_RESET);
        exit(1);
    }

    //------------------------------------------------------------------------------------------------------

    @Override
    public void visitTermExpression(TermExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("Term Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }
    public void visitUnaryExpression(UnaryExpression op) {
    }

    @Override
    public void visitFactorExpression(FactorExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("Factor Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("Primary Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

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
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("Logical Expression Type Error: %s", rhs.line,
            lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitComparisonExpression(ComparisionExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("Comparison Expression Type Error: %s", rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitEqualityCheckExpression(EqualityCheckExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("Equality Check Expression Type Error: %s", rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }
    }

    @Override
    public void visitParameter(Parameter p) {

    }

    @Override
    public void visitArrayInitializer(ArrayInitializer p) {

    }

    @Override
    public void visitParanExpression(ParanExpression p) {
        p.expressions.get(0).typeAnalyse(this);
    }

    @Override
    public void visitConstantVariable(ConstantVariable var) {
        // first analyse if the declaration is okay
        var.declarator.typeAnalyse(this);
        // then check if the declaration type matches the id type
        if(!checkStatementTypes(var.type,var.declarator)){
            reportSemanticError("Constant Variable type does not match the declaration type: %s", var.line,  var.type.toString() + " != " + var.declarator.getType() );
        }


    }

    public void typeCheck(){
        for(ConstantVariable c : program.constantVariables){
            c.typeAnalyse(this);
        }
    }

}
