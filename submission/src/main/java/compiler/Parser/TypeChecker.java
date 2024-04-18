package compiler.Parser;

import com.sun.tools.attach.AgentInitializationException;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static java.lang.System.exit;
import static java.lang.System.in;


/**
* TODO:
 * -> proper error message for missing identifier for variable declarations instead of null pointer exception
 * What I did last:
 * -> missing free statement handling
 * -> missing return statement error is missing
 * -> fix float int promotion
 * -> composite struct accesses in one line
 *  -> variable scopes
 *  -> Child contexts
 *  -> how to update context
 * -> part of  struct accesses
 * -> Next step: Structs to user defined types
 * -> Update error messages
 * - refactored types to GenericType and extended more specific ones
 * - added array declarations
 * - declaration type changes the TypeDec type
 * - RootContext gives info about a variable so you can use idenexpressions
* */


public class TypeChecker implements NodeVisitor{
    Program program;
    ContextGod currContext;
    HashMap<String, UserType> userTypes;
    HashMap<String, ArrayList<ProcedureInfo>> procedureInfos;
    public TypeChecker(Program p ){
        this.program = p;
        this.currContext = new Context();
        this.currContext.setPrev(null);
        this.userTypes = new HashMap<>();
        this.procedureInfos = new HashMap<>();
        // Built-in functions
        // string s = chr(int x);
        ArrayList<GenericType> params = new ArrayList<>();
        params.add(new Type(Token.INTEGER.image(), false));
        ArrayList<ProcedureInfo> infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                        "chr",
                        new Type(Token.STRING.image(), false),
                        params));
        procedureInfos.put("chr", infos);

        // int l = len(string or array);
        params = new ArrayList<>();
        params.add(new Type(Token.STRING.image(), false));
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                        "len",
                        new Type(Token.INTEGER.image(), false),
                        params));
        params = new ArrayList<>();
        params.add(new Type("[]", false));
        infos.add(new ProcedureInfo(
                "len",
                new Type(Token.INTEGER.image(), false),
                params));
        procedureInfos.put("len",infos);

        // int l = floor(float);
        params = new ArrayList<>();
        params.add(new Type(Token.FLOAT.image(), false));
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                "floor",
                new Type(Token.INTEGER.image(), false),
                params));
        procedureInfos.put("floor",infos);

        // int l = readInt();
        params = new ArrayList<>();
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                "readInt",
                new Type(Token.INTEGER.image(), false),
                params));
        procedureInfos.put("readInt",infos);

        // float l = readFloat();
        params = new ArrayList<>();
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                "readFloat",
                new Type(Token.FLOAT.image(), false),
                params));
        procedureInfos.put("readFloat",infos);

        // string l = readString();
        params = new ArrayList<>();
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                "readString",
                new Type(Token.STRING.image(), false),
                params));
        procedureInfos.put("readString",infos);

        // int l = writeInt(int x);
        params = new ArrayList<>();
        params.add(new Type(Token.INTEGER.image(), false));
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                "writeInt",
                new Type(Token.INTEGER.image(), false),
                params));
        procedureInfos.put("writeInt",infos);

        // int l = writeFloat(float x);
        params = new ArrayList<>();
        params.add(new Type(Token.FLOAT.image(), false));
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                "writeFloat",
                new Type(Token.INTEGER.image(), false),
                params));
        procedureInfos.put("writeFloat",infos);

        // int l = write(string x);
        params = new ArrayList<>();
        params.add(new Type(Token.STRING.image(), false));
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                "write",
                new Type(Token.INTEGER.image(), false),
                params));
        procedureInfos.put("write",infos);

        // int l = writeln();
        params = new ArrayList<>();
        infos = new ArrayList<>();
        infos.add(new ProcedureInfo(
                "writeln",
                new Type(Token.INTEGER.image(), false),
                params));
        procedureInfos.put("writeln",infos);


    }
    private void pushContext(ContextGod newContext){
        currContext.setNext(newContext);
        newContext.setPrev(currContext);
        currContext = newContext;
    }

    private void popContext(){
        currContext = currContext.getPrev();
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
        currContext.setConstants_and_globals(currContext.getContextTable());
        for (Procedure p : program.procedures){
            p.typeAnalyse(this);
        }
    }

    private ArrayList<ArrayList<GenericType>> getProcedureParamTypes(String id){
        if(procedureInfos.containsKey(id)){
            ArrayList<ProcedureInfo> infos = procedureInfos.get(id);
            ArrayList<ArrayList<GenericType>> types = new ArrayList<>();
            for(ProcedureInfo info : infos){
                types.add(info.getParameters());
            }
            return types;
        }
        return null;
    }

    private GenericType getProcedureReturnType(String id, int index){
        if(procedureInfos.containsKey(id)){
            ArrayList<ProcedureInfo> infos = procedureInfos.get(id);
            return infos.get(index).getReturnType();
        }
        return null;
    }



    private Boolean checkTypes(GenericType tl, GenericType tr, boolean promoteInt){
        if(userTypes.containsKey(tl.type())){
            tl = userTypes.get(tl.type());
        }

        if(userTypes.containsKey(tr.type())){
            tr = userTypes.get(tr.type());
        }
        if(tl.type().equals(tr.type()) && tl.isArray() == tr.isArray()){
            return true;
        } else if (promoteInt) {
          if (tl.type().equals(Token.FLOAT.image()) && tr.type().equals(Token.INTEGER.image())) {
              return true;
          } else if (tr.type().equals(Token.FLOAT.image()) && tl.type().equals(Token.INTEGER.image())) {
              return true;
          }
        }
        return false;
    }

    // Specifically for float and int
    // previous code made the user be able to convert int declared variabel to float
    // but this additional code only allows now ints to be used with float declared variables
    private Boolean compareForPromotion(GenericType tl, GenericType tr){
        if (tl.type().equals(Token.FLOAT.image()) && tr.type().equals(Token.INTEGER.image())) {
            return true;
        } else if (tr.type().equals(Token.FLOAT.image()) && tl.type().equals(Token.INTEGER.image())) {
            return false;
        }
        return true;
    }


    private Boolean checkExpressionTypes(Expression lhs, Expression rhs){
        if(lhs != null && rhs != null){

            lhs.typeAnalyse(this);
            rhs.typeAnalyse(this);

            GenericType tl = lhs.getType();
            GenericType tr = rhs.getType();
            return checkTypes(tl,tr, true);

        }
        return true;
    }
    private void reportSemanticError(String message,int line, Object... args) throws Error{
        Error e = new Error(String.format("%s:%d: -> Semantic Analysis error: %s", program.getFileName(), line, String.format(message, args)));
        e.printStackTrace();
        try {
            throw e;
        }finally {
            Thread longRunningHook = new Thread(() -> {
                try {
                    Thread.sleep(100);
                    exit(1);
                } catch (InterruptedException ignored) {}
            });
            longRunningHook.start();
        }
    }

    //------------------------------------------------------------------------------------------------------

    @Override
    public void visitTermExpression(TermExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkExpressionTypes(lhs,rhs)){
            reportSemanticError("OperatorError: Term Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }
    public void visitUnaryNegateExpression(UnaryExpression op) {
        // Only boolean
        assert op.lhs == null;
        GenericType tr = op.rhs.getType();
        if(!tr.type().equals("bool")){
            reportSemanticError("OperatorError: Unary Negate Type Error: Cannot negate type %s",op.rhs.line,tr);
        }
    }

    @Override
    public void visitUnaryMinusExpression(UnaryExpression op) {
        // Only float and int
        assert op.lhs == null;
        GenericType tr = op.rhs.getType();
        if(!tr.type().equals("float") && !tr.type().equals("int")){
            reportSemanticError("OperatorError: Unary Minus Type Error: Cannot make minus type %s",op.rhs.line,tr);
        }
    }

    @Override
    public void visitFactorExpression(FactorExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkExpressionTypes(lhs,rhs)){
            reportSemanticError("OperatorError: Factor Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkExpressionTypes(lhs,rhs)){
            reportSemanticError("OperatorError: Primary Expression Type Error: %s",rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitIndexOperation(IndexOp op) {
        op.index.typeAnalyse(this);
        GenericType indexType = op.index.getType();
        if(!indexType.type().equals(Token.INTEGER.image())){
            reportSemanticError("IndexOperationError: Trying to index with non int %s", op.line,indexType);
        }
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression exp) {
        // need to check if it isa struct init, since it is also parsed as functioncall expression in user defined types
        // need to also check if the function exists in the context
        Symbol identifier = exp.identifier;
        ArrayList<Expression> expressions = exp.expressionParams;

        // is it a struct init?
        if(this.userTypes.containsKey(identifier.image()) || this.procedureInfos.containsKey(identifier.image())) {

            ArrayList<GenericType> fields;
            String context;

            if(this.userTypes.containsKey(identifier.image())){
                fields = this.userTypes.get(identifier.image()).fields; 
                context = "Struct init";
            } else {
                fields = this.procedureInfos.get(identifier.image()).get(0).getParameters();
                context = "Function call";
            }
            
            int field_size = fields.size();
            if(field_size != expressions.size()){
                reportSemanticError("ArgumentError: Wrong number of arguments in %s",exp.line,context);
            }
            int i = 0;
            for(Expression e: expressions){
                e.typeAnalyse(this);
                GenericType t0 = e.getType();
                GenericType t1 = fields.get(i);
                if(!checkTypes(t0, t1,false)){
                    reportSemanticError("ArgumentError: Argument type mismatch in %s: %s",exp.line,context,
                            t1.type() + "!=" + t0.type() );
                }
                i++;

            }
        }

    }

    private GenericType checkContext(String id){
        if(currContext.containsId(id)){
            return currContext.getVarType(id);
        }
        if(userTypes.containsKey(id)){
            return userTypes.get(id);
        }
        ContextGod prev = currContext.getPrev();
        while(prev != null){
            if(prev.containsId(id)){
                return prev.getVarType(id);
            }
            prev = prev.getPrev();
        }
        return null;
    }

    @Override
    public GenericType visitSymbolTableIdentifier(IdentifierExpression identifier) {
        String id = identifier.id.image();
        GenericType ret = checkContext(id);
        if(ret == null){
            reportSemanticError("ScopeError : Variable does not exist in context : %s", identifier.line,id); //Not sure about the error type
        }
        
        return ret;

    }

    @Override
    public GenericType visitSymbolTableFunction(FunctionCallExpression functionCallExpression) {
        String id = functionCallExpression.identifier.image();
        GenericType ret = checkContext(id);
        if(ret != null){
            return ret;
        }

        // All this is for len function accepting either array or string :)
        ArrayList<ArrayList<GenericType>> typesAll = getProcedureParamTypes(id);
        if(typesAll == null){
            reportSemanticError("ScopeError : Function does not exist in context : %s", functionCallExpression.line,id);
        }
        ArrayList<Expression> params = functionCallExpression.expressionParams;
        for(ArrayList<GenericType> types: typesAll){
            if(types != null){
                if(params.size() > 1){
                    reportSemanticError("ScopeError : Function does not exist in context : %s", functionCallExpression.line,id);
                }
                else{
                    // for functions that have no arguments
                    if(params.isEmpty()&&types.isEmpty()){
                        return getProcedureReturnType(id,0);
                    }else{
                        params.get(0).typeAnalyse(this);
                        GenericType t = params.get(0).getType();
                        int index = 0;
                        for(GenericType type: types){
                            boolean c0 = type.type().equals("[]");
                            boolean c1 = params.get(0).getType().isArray();
                            if( c0 && c1 ){
                                return getProcedureReturnType(id,index);
                            }
                            if(type.type().equals(t.type())){
                                return getProcedureReturnType(id,index);
                            }
                            index++;
                        }
                    }
                }
            }
        }
        reportSemanticError("ScopeError : Function does not exist in context : %s", functionCallExpression.line,id);
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
        op.prettyPrint("");
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        lhs.typeAnalyse(this); // gets the struct type
        GenericType lhsType = lhs.getType();
        if(userTypes.containsKey(lhsType.type())){
            UserType structType = userTypes.get(lhsType.type());
            String member;
            GenericType t = null;
            if(rhs.lhs != null){
                Expression one_level_down = rhs;
                while(one_level_down.lhs != null && one_level_down.rhs != null){
                    Expression level_lhs = one_level_down.lhs;
                    GenericType member1_type = structType.members.get(level_lhs.getRep());
                    if(userTypes.containsKey(member1_type.type())){
                        structType = userTypes.get(member1_type.type());
                        one_level_down = one_level_down.rhs;
                        if(one_level_down instanceof IndexOp){
                            ((IndexOp) one_level_down).index.typeAnalyse(this);
                            String type =  structType.members.get(((IndexOp) one_level_down).identifier.image()).type();
                            t = new Type(type,false);
                        }else{
                            t = structType.members.get(one_level_down.getRep());
                        }

                    }else{
                        one_level_down = one_level_down.rhs;
                        t = structType.members.get(member1_type.type());
                    }

                }

            }
            else{
                member = rhs.getRep();
                t = structType.members.get(member);
            }

            //if(!structType.members.containsKey(member)){
            //    reportSemanticError("Member does not exist or composite struct access not supported: %s",
            //            lhs.line,member);
            //}
            return t;
        }
        return new Type("ERROR MORUK IN DOT OP SYMBOL TABLE", false);
    }
//    @Override
//    public GenericType visitSymbolTableDotOp_old(DotOperation op) {
//        op.prettyPrint("");
//        Expression lhs = op.lhs;
//        Expression rhs = op.rhs;
//        lhs.typeAnalyse(this); // gets the struct type
//        GenericType lhsType = lhs.getType();
//        if(userTypes.containsKey(lhsType.type())){
//            UserType structType = userTypes.get(lhsType.type());
//            String member = rhs.getRep();
//            if(!structType.members.containsKey(member)){
//                reportSemanticError("Member does not exist or composite struct access not supported: %s",
//                        lhs.line,member);
//            }
//            GenericType t = structType.members.get(member);
//            return t;
//        }
//        return new Type("ERROR MORUK IN DOT OP SYMBOL TABLE", false);
//    }



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
    }

    @Override
    public void visitLogicalExpression(LogicalExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkExpressionTypes(lhs,rhs)){
            reportSemanticError("OperatorError: Logical Expression Type Error: %s", rhs.line,
            lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitComparisonExpression(ComparisionExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkExpressionTypes(lhs,rhs)){
            reportSemanticError("OperatorError: Comparison Expression Type Error: %s", rhs.line,
                    lhs.getType().toString() + " is not compatible with " + rhs.getType().toString());
        }

    }

    @Override
    public void visitEqualityCheckExpression(EqualityCheckExpression op) {
        Expression lhs = op.lhs;
        Expression rhs = op.rhs;
        if(!checkExpressionTypes(lhs,rhs)){
            reportSemanticError("OperatorError: Equality Check Expression Type Error: %s", rhs.line,
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
    public void visitFreeStatement(FreeStatement st) {
        GenericType freedType = st.getType();
        if(userTypes.containsKey(freedType.type())||freedType.isArray()){
            return;
        }
        reportSemanticError("FreeError: Cannot free type: %s", st.line, freedType.type());
        //if(userTypes.containsKey(freedType.type()) && !freedType.isArray()){
        //    reportSemanticError("FreeError: Cannot free non-array built in type: %s", st.line, freedType.type());
        //}
        //else if(!userTypes.containsKey(freedType.type())){
        //    reportSemanticError("FreeError: Cannot free user defined type: %s", st.line, freedType.type());
        //}
    }

    @Override
    public void visitConstantVariable(VariableGod var) {
        // we know that if parser parsed the constant or global it is initialized
        // first analyse if the declaration is okay
        var.declarator().typeAnalyse(this);
        // then check if the declaration type matches the id type
        GenericType decType = var.declarator().getType();
        GenericType varType = new Type(var.typeDeclaration().type.image(),var.typeDeclaration().isArray);
        if(!checkTypes(varType, decType,true) || !compareForPromotion(varType,decType)){
            reportSemanticError("TypeError: Constant Variable type does not match the declaration type: %s",
                    var.line,  var.typeDeclaration().toString() + " != " + var.declarator().getType() );
        }
        // add to the context
        Type t = new Type(decType.type(),var.typeDeclaration().isArray);
        t.setIsConstant(true);
        if(!currContext.addToContext(var.identifier().getRep(),t)){
           reportSemanticError("ScopeError: The variable %s already exists in context",
                   var.identifier().line,
                   var.identifier().getRep());
        }
    }

    @Override
    public void visitVariable(VariableGod var) {
        String id = var.identifier().getRep();
        if(currContext.getConstants_and_globals().containsKey(id)){
            reportSemanticError("ScopeError : Trying to create a variable with the same name as one " +
                    "of the globals or constants: %s", var.line,id ); //TODO
        }
        // whether it is an uninit variable or not
        if(var.declarator() != null){
            var.declarator().typeAnalyse(this);
            GenericType decType = var.declarator().getType();
            GenericType varType = new Type(var.typeDeclaration().type.image(),var.typeDeclaration().isArray);
            if(!checkTypes(varType, decType,true) || !compareForPromotion(varType,decType)){
                reportSemanticError("TypeError: Global Variable type does not match the declaration type: %s",
                        var.line,  var.typeDeclaration().toString() + " != " + var.declarator().getType() );
            }
        }
        // add to the context
        if(!currContext.addToContext(var.identifier().getRep(),
                new Type(var.typeDeclaration().type.image(),var.typeDeclaration().isArray))){
            reportSemanticError("ScopeError: Variable already exists in context : %s",
                    var.identifier().line,
                    var.identifier().getRep());
        }

    }

    @Override
    public void visitScopeVariable(ScopeVariable var) {
        Expression declarator = var.declarator;
        Expression identifier = var.identifier;
        declarator.typeAnalyse(this);
        identifier.typeAnalyse(this);
        GenericType varType = identifier.getType();
        GenericType decType = declarator.getType();
        if(!checkTypes(varType, decType,true) || !compareForPromotion(varType,decType)){
            reportSemanticError("TypeError: Scope Variable type does not match the declaration type: %s",
                    var.line,  varType + " != " + var.declarator().getType() );
        }
    }

    @Override
    public void visitStructDeclaration(StructDeclaration s) {
        Expression identifier = s.identifier;
        Block block = s.block;

        if(identifier.getRep().equals("while") || identifier.getRep().equals("if") || identifier.getRep().equals("for")){
            reportSemanticError("StructError: Structure declaration overwirte existing types : %s", s.line, identifier.getRep());
        }
        else if(userTypes.containsKey(identifier.getRep())) {
            reportSemanticError("StructError: Structure declaration overwirte a previously defined structure : %s", s.line, identifier.getRep());
        }
        this.userTypes.put(identifier.getRep(),new UserType(block));
    }

    @Override
    public void visitProcedure(Procedure p) {
        TypeDeclaration typeDeclaration = p.returnType;
        Symbol identifier = p.identifier;
        ProcedureDeclarator declarator = p.declarator;
        ArrayList<Expression> parameters = declarator.parameters;

        GenericType ret = new Type(typeDeclaration.type.image(),typeDeclaration.isArray);
        currContext.addToContext(identifier.image(),ret);

        ProcedureContext procedureContext = new ProcedureContext(parameters);
        procedureContext.setContextName(identifier.image());
        pushContext(procedureContext);

        Block block = declarator.block;
        for(Statement s : block.statements){
            s.typeAnalyse(this);
        }
        // Check the last statement, it needs to be a return if not ret is void
        if(!ret.type().equals("void")){
            Statement last = block.statements.get(block.statements.size()-1);
            if(!(last instanceof ReturnStatement)){
                reportSemanticError("ReturnError : Missing return, please return with type: %s ", block.line, ret.type());
            }

        }


        popContext();

        ArrayList<GenericType> types = new ArrayList<>();
        for(Map.Entry<String, GenericType> entry : procedureContext.arguments.entrySet()) {
            String key = entry.getKey();
            GenericType value = entry.getValue();
            types.add(value);
        }

        ProcedureInfo newInfo = new ProcedureInfo(identifier.image(), ret, types );
        ArrayList<ProcedureInfo> newInfos = new ArrayList<>();
        newInfos.add(newInfo);
        procedureInfos.put(identifier.image(), newInfos);
    }

    @Override
    public void visitReturn(ReturnStatement ret) {
        Expression retExp = ret.expression;
        retExp.typeAnalyse(this);

        if(retExp.getClass() == LiteralExpression.class && ((LiteralExpression) retExp).literal == null){
            reportSemanticError("ReturnError : Return expression is empty", retExp.line);
        }

        GenericType retType = retExp.getType();
        String contextName = currContext.getContextName();
        if(!currContext.containsId(contextName)){
            reportSemanticError("ReturnError : Trying to return in a strange way without a function context", ret.line);
        }
        GenericType functionRetType = currContext.getVarType(contextName);
        if(userTypes.containsKey(functionRetType.type())){
            functionRetType = userTypes.get(functionRetType.type());
        }
        if(!checkTypes(retType, functionRetType, false)){
            reportSemanticError("ReturnError : Function return type mismatch: %s expects %s not %s"
                    ,ret.line,contextName,functionRetType.type(),retType.type());
        }
    }
    @Override
    public void visitWhile(WhileStatement w) {
        Expression condition = w.condition;
        condition.typeAnalyse(this);
        Block block = w.block;

        Context whileContext = new Context();
        whileContext.setContextName(currContext.getContextName());
        pushContext(whileContext);

        visitCondition(condition, "While");

        if(block != null){
            for(Statement s : block.statements){
                s.typeAnalyse(this);
            }
        }
        popContext();
    }

    @Override
    public void visitIfElse(IfElseStatement ifelse) {
        Expression condition = ifelse.ifCondition;
        condition.typeAnalyse(this);
        Block ifBlock = ifelse.ifBlock;
        Block elseBlock = ifelse.elseBlock;

        visitCondition(condition, "IfElse");

        if(ifBlock != null){
            Context ifContext = new Context();
            ifContext.setContextName(currContext.getContextName());
            pushContext(ifContext);
            for(Statement s : ifBlock.statements){
                s.typeAnalyse(this);
            }
            popContext();
        }
        if(elseBlock != null){
            Context elseContext = new Context();
            elseContext.setContextName(currContext.getContextName());
            pushContext(elseContext);
            for(Statement s : elseBlock.statements){
                s.typeAnalyse(this);
            }
            popContext();
        }

    }

    @Override
    public void visitFor(ForStatement forStatement) {
        Statement pos0 = forStatement.pos0;
        Expression pos1 = forStatement.pos1;
        Statement pos2 = forStatement.pos2;
        Block forBlock = forStatement.block;
        if(pos0.toString().equals("LiteralExp(null)") ||pos2.toString().equals("LiteralExp(null)")){
            reportSemanticError("MissingConditionError : one of the statements in %s statement is empty", pos0.line, "For");
        }

        pos0.typeAnalyse(this);
        pos1.typeAnalyse(this);
        pos2.typeAnalyse(this);

        visitCondition(pos1, "For");

        if(forBlock != null){
            Context forContext = new Context();
            forContext.setContextName(currContext.getContextName());
            pushContext(forContext);
            for(Statement s : forBlock.statements){
                s.typeAnalyse(this);
            }
            popContext();
        }
    }

    public void visitCondition(Expression condition, String context){
        condition.typeAnalyse(this);
        if(condition.getClass() == LiteralExpression.class && ((LiteralExpression) condition).literal.token() == Token.NULL){
            reportSemanticError("MissingConditionError : Condition in %s statement is empty", condition.line, context);
        }

        if(!checkTypes(condition.getType(),new Type("bool",false) , false)){
            reportSemanticError("MissingConditionError :  Condition is in %s statement not a boolean expression : %s", condition.line, context, condition.getType().type());
        }
    }

    public void debug(){
        currContext.debugContext("");
        System.out.println("User types:");
        System.out.println("  "+this.userTypes);
        //System.out.println("Procedure Context Hash Map");

        //for(Map.Entry<String, ContextGod> entry : procedureContextHashMap.entrySet()) {
        //    String key = entry.getKey();
        //    ContextGod value = entry.getValue();
        //    value.debugContext(" ");
        //}
    }
}
