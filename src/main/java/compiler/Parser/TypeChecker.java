package compiler.Parser;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.exit;
import static java.lang.System.in;


/**
* TODO:
 * -> struct accesses
 * Procedures:
 *  -> Child contexts
 *  -> how to update context
 * <p>
 * What I did last:
 * -> part of  struct accesses
 * -> Next step: Structs to user defined types
 * -> Update error messages
 * - refactored types to GenericType and extended more specific ones
 * - added array declarations
 * - declaration type changes the TypeDec type
 * - RootContext gives info about a variable so you can use idenexpressions
* <p>
* */


public class TypeChecker implements NodeVisitor{
    Program program;
    ContextGod currContext;
    ContextGod prevContext;
    HashMap<String, UserType> userTypes;
    public TypeChecker(Program p ){
        this.program = p;
        this.currContext = new Context(null);
        this.userTypes = new HashMap<>();
        prevContext = null;
    }
    // TODO:
    private void updateContext(){

    }

    public void typeCheck(){
        for(ConstantVariable c : program.constantVariables){
            c.typeAnalyse(this);
        }

        for(StructDeclaration sd : program.structDeclarations){
            sd.typeAnalyse(this);
        }
        for(VariableGod v : program.globals){
            v.typeAnalyse(this);
        }
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
            case "<IDENTIFIER>" -> {
                return equals;
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
            reportSemanticError("TypeError: Term Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }
    public void visitUnaryNegateExpression(UnaryExpression op) {
        // Only boolean
        assert op.lhs == null;
        GenericType tr = op.rhs.getType();
        if(!tr.type().equals("bool")){
            reportSemanticError("TypeError: Unary Negate Type Error: Cannot negate type %s ",op.rhs.line,tr);
        }
    }

    @Override
    public void visitUnaryMinusExpression(UnaryExpression op) {
        // Only float and int
        assert op.lhs == null;
        GenericType tr = op.rhs.getType();
        if(!tr.type().equals("float") && !tr.type().equals("int")){
            reportSemanticError("TypeError: Unary Minus Type Error: Cannot make minus type %s ",op.rhs.line,tr);
        }
    }

    @Override
    public void visitFactorExpression(FactorExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("TypeError: Factor Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("TypeError: Primary Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitIndexOperation(IndexOp op) {
        op.index.typeAnalyse(this);
        GenericType indexType = op.index.getType();
        if(!indexType.type().equals(Token.INTEGER.image())){
            reportSemanticError("Index Operation error: Trying to index with non int %s", op.line,indexType);
        }
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression exp) {
        // need to check if it isa struct init, since it is also parsed as functioncall expression in user defined types
        // need to also check if the function exists in the context
        Symbol identifier = exp.identifier;
        ArrayList<Expression> expressions = exp.expressionParams;

        // is it a struct init?
        if(this.userTypes.containsKey(identifier.image())){
            ArrayList<GenericType> fields = this.userTypes.get(identifier.image()).fields;
            int field_size = fields.size();
            if(field_size != expressions.size()){
                reportSemanticError("StructError: Wrong number of arguments",exp.line);
            }
            int i = 0;
            for(Expression e: expressions){
                e.typeAnalyse(this);
                String typeGiven = e.getType().toString();
                String typeActual = fields.get(i).toString();
                if(!typeActual.equals(typeGiven)){
                    reportSemanticError("StructError: Argument type mismatch: %s",exp.line, typeActual + "!=" + typeGiven );
                }
                i++;

            }

        }


    }


    @Override
    public GenericType visitSymbolTableIdentifier(IdentifierExpression identifier) {
        String id = identifier.id.image();
        if(currContext.containsId(id)){
            return currContext.getVarType(id);
        }
        // TODO: what to do when the variable does not exist in the current context
        return null;

    }

    @Override
    public GenericType visitSymbolTableFunction(FunctionCallExpression functionCallExpression) {
        String id = functionCallExpression.identifier.image();
        if(currContext.containsId(id)){
            return currContext.getVarType(id);
        }
        if(userTypes.containsKey(id)){
            return new Type(id,false);
        }
        // TODO: what to do when the variable does not exist in the current context
        return null;
    }

    @Override
    public GenericType visitSymbolTableIndexOp(IndexOp op) {
        // is it actual index operation
        // or is it struct array init
        if(userTypes.containsKey(op.identifier.image())){
            return new Type(op.identifier.image(),true); // since we know it is an array init of struct
        } else if (currContext.containsId(op.identifier.image())) {
            GenericType arrayType = currContext.getVarType(op.identifier.image());
            return new Type(arrayType.type(),false);
        }
        else{
            reportSemanticError("ERROR IN VISIT SYMBOL TABLE INDEX OP", op.line);
        }
        return null;
    }

    @Override
    public GenericType visitSymbolTableDotOp(DotOperation op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        lhs.typeAnalyse(this); // gets the struct type
        GenericType lhsType = lhs.getType();
        if(userTypes.containsKey(lhsType.type())){
            UserType structType = userTypes.get(lhsType.type());
            String member = rhs.getRep();
            if(!structType.members.containsKey(member)){
                reportSemanticError("You are trying to access the member of a struct that does not exist: %s", lhs.line,member);
            }
            GenericType t = structType.members.get(member);
            return t;
        }
        return new Type("ERROR MORUK IN DOT OP SYMBOL TABLE", false);
    }

    @Override
    public void visitDotOperation(DotOperation op) {
        /**
         * - Could be struct access
         * - could be struct access inside struct (structception)
         * So ideally:
         * * - check the variable type in currContext
         * * - throw error if not a struct
         * ...
        * */
        //Expression lhs = op.lhs;
        //Expression rhs = op.rhs;
        //lhs.typeAnalyse(this); // gets the struct type
        //GenericType lhsType = lhs.getType();
        //if(userTypes.containsKey(lhsType.type())){
        //    UserType structType = userTypes.get(lhsType.type());
        //    String member = rhs.getRep();
        //    if(!structType.members.containsKey(member)){
        //        reportSemanticError("You are trying to access the member of a struct that does not exist: %s", lhs.line,member);
        //    }

        //}
    }

    @Override
    public void visitLogicalExpression(LogicalExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("TypeError: Logical Expression Type Error: %s", rhs.line,
            lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitComparisonExpression(ComparisionExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("TypeError: Comparison Expression Type Error: %s", rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitEqualityCheckExpression(EqualityCheckExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkTypes(lhs,rhs)){
            reportSemanticError("TypeError: Equality Check Expression Type Error: %s", rhs.line,
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
    public void visitConstantVariable(VariableGod var) {
        // we know that if parser parsed the constant or global it is initialized
        // first analyse if the declaration is okay
        var.declarator().typeAnalyse(this);
        // then check if the declaration type matches the id type
        GenericType decType = var.declarator().getType();
        if(!checkStatementTypes(var.typeDeclaration(),decType)){
            reportSemanticError("TypeError: Constant Variable type does not match the declaration type: %s",
                    var.line,  var.typeDeclaration().toString() + " != " + var.declarator().getType() );
        }
        // add to the context
        Type t = new Type(decType.type(),var.typeDeclaration().isArray);
        t.setIsConstant(true);
        if(!currContext.addToContext(var.identifier().getRep(),t
                )){
           reportSemanticError("ScopeError: The variable %s already exists in context",
                   var.identifier().line,
                   var.identifier().getRep());
        }
    }

    @Override
    public void visitGlobalVariable(VariableGod var) {
        var.declarator().typeAnalyse(this);
        GenericType decType = var.declarator().getType();
        if(!checkStatementTypes(var.typeDeclaration(),decType)){
            reportSemanticError("TypeError: Global Variable type does not match the declaration type: %s",
                    var.line,  var.typeDeclaration().toString() + " != " + var.declarator().getType() );
        }
        // add to the context
        if(!currContext.addToContext(var.identifier().getRep(),
                new Type(decType.type(),var.typeDeclaration().isArray))){
            reportSemanticError("ScopeError: The variable %s already exists in context",
                    var.identifier().line,
                    var.identifier().getRep());
        }

    }

    @Override
    public void visitStructDeclaration(StructDeclaration s) {
        Expression identifier = s.identifier;
        Block block = s.block;
        this.userTypes.put(identifier.getRep(),new UserType(block));
    }



    public void debug(){
        currContext.debugContext("");
        System.out.println("User types:");
        System.out.print("  "+this.userTypes);
    }
}
