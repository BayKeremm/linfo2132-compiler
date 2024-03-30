package compiler.Parser;

import compiler.Lexer.Token;
import static java.lang.System.exit;


/**
* TODO:
 * Next step: Structs to user defined types
 * Procedures:
 *  - Child contexts
 *  - how to update context
 *
 * What I did last:
 * -
 * - refactored types to GenericType and extended more specific ones
 * - added array declarations
 * - declaration type changes the TypeDec type
 * - RootContext gives info about a variable so you can use idenexpressions
*
* */


public class TypeChecker implements NodeVisitor{
    Program program;
    Context rootContext;
    public TypeChecker(Program p ){
        this.program = p;
        this.rootContext = new Context(null);
    }
    private Boolean checkTypes(Expression lhs, Expression rhs){
        if(lhs != null && rhs != null){

            lhs.typeAnalyse(this);
            rhs.typeAnalyse(this);

            GenericType tl = lhs.getType();
            GenericType tr = rhs.getType();

            if(tl.type().equals(tr.type())){
                return true;
            } else if (tl.type().equals(Token.FLOAT.image()) && tr.type().equals(Token.INTEGER.image())) {
                return true;
            } else if (tr.type().equals(Token.FLOAT.image()) && tl.type().equals(Token.INTEGER.image())) {
                return true;
            }
            return false;
        }
        return true;
    }
    private Boolean checkStatementTypes(TypeDeclaration idType, GenericType dec_type){
        String type = idType.type.token().image();
        final boolean equals = idType.toString().equals(dec_type.toString());
        switch (type) {
            case "int", "float" -> {
                return dec_type.toString().equals(Token.INTEGER.image())
                        || dec_type.toString().equals(Token.FLOAT.image())
                        || equals;
            }
            case "string" -> {
                return dec_type.toString().equals(Token.STRING.image())
                        || equals;
            }
            case "bool" -> {
                return dec_type.toString().equals(Token.BOOLEAN.image())
                        || equals;
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
    public void visitUnaryNegateExpression(UnaryExpression op) {
        // Only boolean
        assert op.lhs == null;
        GenericType tr = op.rhs.getType();
        if(!tr.type().equals("bool")){
            reportSemanticError("Unary Negate Type Error: Cannot negate type %s ",op.rhs.line,tr);
        }
    }

    @Override
    public void visitUnaryMinusExpression(UnaryExpression op) {
        // Only float and int
        assert op.lhs == null;
        GenericType tr = op.rhs.getType();
        if(!tr.type().equals("float") && !tr.type().equals("int")){
            reportSemanticError("Unary Minus Type Error: Cannot make minus type %s ",op.rhs.line,tr);
        }
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
    public GenericType visitIdentifierExpression(IdentifierExpression identifier) {
        String id = identifier.id.image();
        if(rootContext.containsId(id)){
            return rootContext.getVarType(id);
        }
        // TODO: what to do when the variable does not exist in the current context
        return null;

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
        GenericType decType = var.declarator.getType();
        if(!checkStatementTypes(var.typeDecl,decType)){
            reportSemanticError("Constant Variable type does not match the declaration type: %s",
                    var.line,  var.typeDecl.toString() + " != " + var.declarator.getType() );
        }
        // add to the context
        // TODO: What to when the same variable is already in the context
        rootContext.addVariable(var.identifier.getRep(), new Type(decType.type(),var.typeDecl.isArray));
    }

    @Override
    public void visitStructDeclaration(StructDeclaration s) {
        Expression identifier = s.identifier;
        Block block = s.block;
        // TODO: Put it into user defined types
        rootContext.addVariable(identifier.getRep(),new UserType(block));
    }

    public void typeCheck(){
        for(ConstantVariable c : program.constantVariables){
            c.typeAnalyse(this);
        }

        for(StructDeclaration sd : program.structDeclarations){
            sd.typeAnalyse(this);
        }
    }


    public void debug(){
        rootContext.debugContext("    ");
    }
}
