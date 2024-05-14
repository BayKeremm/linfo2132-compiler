package compiler.bytecodegen;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.*;
import compiler.Parser.expressions.*;
import compiler.Parser.statements.*;
import compiler.semantics.ProcedureInfo;
import compiler.semantics.Type;
import compiler.semantics.UserType;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static java.lang.System.exit;


public class ByteCodeWizard implements ByteVisitor{

    private Program program;
    private String programName;
    private Boolean localVariable = false;
    private Boolean structDeclarations = false;


    HashMap<String, ArrayList<ProcedureInfo>> procedureInfos;
    HashMap<String, GenericType> constants;
    HashMap<String, GenericType> globals;
    HashMap<String, StructDeclaration> structs;
    LinkedHashMap<String, LinkedHashMap<String, GenericType>> structFields;


    ASMHelper asmHelper;


    public ByteCodeWizard(Program program, String className){
        this.program = program;
        this.programName = className;
        this.procedureInfos = new HashMap<>();
        this.constants = new HashMap<>();
        this.globals = new HashMap<>();
        this.structs = new HashMap<>();
        this.structFields = new LinkedHashMap<>();
        addBuiltInFunctions();
        for(ConstantVariable constantVariable: program.constantVariables){
            constants.put(constantVariable.getVariableName(),
                    new Type(constantVariable.typeDeclaration().getTypeSymbol().image(),
                            constantVariable.typeDeclaration().getIsArray()));
        }
        for(VariableGod variable: program.globals){
            globals.put(variable.getVariableName(),
                    new Type(variable.typeDeclaration().getTypeSymbol().image(),
                            variable.typeDeclaration().getIsArray()));
        }
        for(StructDeclaration s : program.structDeclarations){
           structs.put(s.getStructIdentifier().getRep(), s);
           LinkedHashMap<String, GenericType> fields = new LinkedHashMap<>();
           for(Statement e: s.getStructBlock().getStatements()){
                fields.put(e.getVariableName(),e.getType());
           }
           structFields.put(s.getStructIdentifier().getRep(), fields);

        }
        asmHelper = new ASMHelper(programName, constants, globals, structs);
    }

    public void codeGen(){
        program.codeGen(this);
    }

    public void visitProgram(Program program){
        // Generate a class for each struct type
        structDeclarations = true;
        for(var d: program.structDeclarations){
            d.codeGen(this);
        }
        structDeclarations = false;

        // START THE MAIN CLASS
        asmHelper.startClassWriter();

        if(!constants.isEmpty() || !globals.isEmpty()){
            asmHelper.startStaticInitializer();
            for(ConstantVariable constantVariable: program.constantVariables){
                constantVariable.codeGen(this);
            }
            for(VariableGod var: program.globals){
                var.codeGen(this);
            }
            asmHelper.endMethodVisitor();
        }


        // Go through all the procedures
        this.localVariable=true;

        for(Procedure procedure: program.procedures){
            procedure.codeGen(this);
        }

        asmHelper.writeClass();
    }

    @Override
    public void visitProcedure(Procedure procedure) {
        Type type = new Type(procedure.getProcedureReturnType().getTypeSymbol().image(),
                procedure.getProcedureReturnType().getIsArray());

        String returnSig = asmHelper.getSignature(type.type(), null);
        if(type.isArray()){
            returnSig = "["+returnSig;
        }

        ArrayList<Expression> params = procedure.getProcedureDeclarator().getProDecParameters();
        StringBuilder sig = new StringBuilder();
        sig.append("(");
        for(var e : params){
            if(e.getType().isArray()){
                sig.append("[");
            }
            sig.append(asmHelper.getSignature(e.getType().type(), null));
        }
        sig.append(")");
        sig.append(returnSig);

        asmHelper.startProcedureMethod(procedure.getProcedureIdentifier().image(), String.valueOf(sig));

        asmHelper.startNewScope();

        for(var e : params) {
            asmHelper.addLocalToScope(e.getVariableName(), e.getType());
        }

        ArrayList<Statement> statements = procedure.getProcedureDeclarator().
                getProDecBlock().getStatements();

        for (Statement st : statements){
            st.codeGen(this);
        }

        asmHelper.popScope();
        asmHelper.endMethodVisitor();
    }

    @Override
    public void visitVariable(Variable variable) {
        String name = variable.getVariableName();
        Expression declarator = variable.declarator();
        if(localVariable){
            GenericType type = declarator.getType();
            asmHelper.addLocalToScope(name, type);
            String signature = asmHelper.getLocalSignature(variable);
            if(type.isArray()){
               signature = "["+signature;
            }
            asmHelper.visitLocalVariable(name, signature);
            declarator.codeGen(this);
            asmHelper.storeLocalVariable(name);
        }else{
            String signature = asmHelper.getTypeSignatureStatic(variable);
            asmHelper.visitGlobalVariable(name, signature);
            declarator.codeGen(this);
            asmHelper.storeStaticField(name,signature);
        }
    }

    @Override
    public void visitUnInitVariable(UninitVariable variable) {
        String name = variable.getVariableName();
        GenericType type = variable.getType();
        if(localVariable){
            asmHelper.addLocalToScope(name, type);
            String signature = asmHelper.getUnInitSignature(name, type);
            asmHelper.visitLocalVariable(name, signature);
            asmHelper.pushDefaultValue(name);
            asmHelper.storeLocalVariable(name);
        }else if(structDeclarations){
            String signature = asmHelper.getUnInitSignature(name, type);
            asmHelper.visitStructField(name, signature);
        }else{
            System.err.println("SIKE, YOU CANNOT HAVE UNINIT VAR OUTSIDE PROCEDURES");
            exit(1);
        }

    }

    @Override
    public void visitConstantVariable(ConstantVariable variable) {
        String signature = asmHelper.getTypeSignatureStatic(variable);
        String name = variable.getVariableName();
        Expression declarator = variable.declarator();
        asmHelper.visitConstantVariable(name,signature);
        declarator.codeGen(this);
        asmHelper.storeStaticField(name,signature);
    }

    @Override
    public void prepDotOp(DotOperation op) {
        String name = op.getRep();
        String fieldName = op.getRhs().getRep();
        GenericType fieldType = structFields.get(op.getLhs().getType().type())
                .get(fieldName);
        GenericType userType = op.getLhs().getType();

        asmHelper.loadStruct(name);
    }

    @Override
    public void prepIndexOp(IndexOp op) {
        Expression index  = op.getIndex();
        GenericType type = op.getType();
        Symbol identifier = op.getIndexIdentifier();
        String name = identifier.image();
        if(constants.containsKey(name) || globals.containsKey(name)){
            String sig = asmHelper.getSignature(type.type(), null);
            // We know it is an array
            sig = "["+sig;
            //asmHelper.getStaticFieldArray(identifier.image(), type.type());
            asmHelper.getStaticField(identifier.image(), sig);
        }
        else if(localVariable){
            asmHelper.loadLocalVariable(identifier.image());
        }
        index.codeGen(this);

    }

    @Override
    public void visitReturn(ReturnStatement ret) {
        ret.getReturnExpression().codeGen(this);
        asmHelper.performReturn(ret.getReturnExpression().getType());
    }

    @Override
    public void visitBreak(BreakStatement brk) {
        asmHelper.jumpToEnd();
    }

    @Override
    public void visitScopeVariable(ScopeVariable variable) {
        boolean arr = false;

        Expression declarator = variable.getScopeDeclarator();
        Expression identifier = variable.getScopeIdentifier();
        String type = identifier.getType().type();

        String name = identifier.getRep();
        int index = name.indexOf('[');
        if (index != -1) {
            arr = true;
            name = name.substring(0, index);
        }

        identifier.prepCodeGen(this);

        checkConstantModify(name);

        declarator.codeGen(this);

        if(globals.containsKey(name)){
            if(identifier instanceof DotOperation){
                String fieldName = identifier.getRhs().getRep();
                GenericType fieldType = structFields.get(identifier.getLhs().getType().type())
                        .get(fieldName);
                GenericType userType = identifier.getLhs().getType();
                //asmHelper.getStructField(name, userType.type() );
                asmHelper.getStaticField(name, "L"+userType.type()+";" );
                declarator.codeGen(this);
                asmHelper.putStructField(userType.type(),fieldName, fieldType);
            }
            else if(arr){
                asmHelper.storeStaticFieldArray(type);
            }else{
                String signature = asmHelper.getSignature(type, declarator);
                asmHelper.storeStaticField(name, signature);
            }
        }
        else if(localVariable){
            // updating sturct field
            if(identifier instanceof DotOperation){
                String fieldName = identifier.getRhs().getRep();
                GenericType fieldType = structFields.get(identifier.getLhs().getType().type())
                        .get(fieldName);
                GenericType userType = identifier.getLhs().getType();
                asmHelper.putStructField(userType.type(),fieldName, fieldType);
            }else{
                asmHelper.updateLocalVariable(name);
            }
        }
    }

    public void checkConstantModify(String name){
        if(constants.containsKey(name)){
            final String ANSI_RESET = "\u001B[0m";
            final String ANSI_RED = "\u001B[31m";
            System.err.print(ANSI_RED);
            System.err.printf("ConstantError: --> Cannot modify constants: %s", name);
            System.err.print(ANSI_RESET);
            exit(1);
        }
    }


    @Override
    public void visitIdentifier(IdentifierExpression expression) {
        String name = expression.getIdentifierSymbol().image();
        GenericType type = null;
        Boolean constant = false;
        if(constants.containsKey(name) ){
            type =  constants.get(name);
            constant = true;
        }else if(globals.containsKey(name)){
            type =  globals.get(name);
            constant = true;
        }
        // Constant
        if(constant){
            String sig = asmHelper.getSignature(type.type(), null);
            if(type.isArray()){
                sig = "["+sig;
            }
            asmHelper.getStaticField(name, sig);
        }else{
            asmHelper.loadLocalVariable(name);
        }
    }




    @Override
    public void visitArrayInit(ArrayInitializer init) {
        GenericType type = init.getType();
        Expression size = init.getSize();
        size.codeGen(this);
        asmHelper.visitArrayInit(type.type());
    }

    @Override
    public void visitIndexOp(IndexOp op) {
        Expression index  = op.getIndex();
        String type = op.getType().type();
        Symbol identifier = op.getIndexIdentifier();
        String name = identifier.image();

        // if identifier in usertypes then it is struct array
        if(structs.containsKey(name)){
            //asmHelper.loadLocalVariable(name);
            index.codeGen(this);
            asmHelper.visitArrayInitStruct(type);
        }else{
            GenericType variableType = null;
            if (constants.containsKey(name)) {
                variableType = constants.get(name);
            } else if (globals.containsKey(name)) {
                variableType = globals.get(name);
            }

            if (variableType != null) {
                String sig = asmHelper.getSignature(variableType.type(), null);
                if (variableType.isArray()) {
                    sig = "[" + sig;
                }
                asmHelper.getStaticField(name, sig);
            } else if (localVariable) {
                asmHelper.loadLocalVariable(name);
            }

            index.codeGen(this);
            switch (type){
                case "int":
                    asmHelper.performSingleOp(Opcodes.IALOAD);
                    break;
                case "float":
                    asmHelper.performSingleOp(Opcodes.FALOAD);
                    break;
                case "string":
                    asmHelper.performSingleOp(Opcodes.AALOAD);
                    break;
                case "bool":
                    asmHelper.performSingleOp(Opcodes.BALOAD);
                    break;
                default:
                    // TODO:
                    asmHelper.performSingleOp(Opcodes.AALOAD);
                    break;

            }

        }

    }

    public void visitLiteral(LiteralExpression exp){
        String lit = exp.getLiteral().image();
        asmHelper.pushLiteral(lit);

    }

    public void codegenSides(Expression lhs, Expression rhs){
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();
        lhs.codeGen(this);
        if(rhs_type.type().equals("float") && lhs_type.type().equals("int")){
            asmHelper.turnIntToFloat();
        }
        rhs.codeGen(this);
        if(rhs_type.type().equals("int") && lhs_type.type().equals("float")){
            asmHelper.turnIntToFloat();
        }
    }

    @Override
    public void visitPlus(PlusOperation plus) {
        Expression lhs = plus.getLhs();
        Expression rhs = plus.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        codegenSides(lhs, rhs);


        asmHelper.performStackOp(lhs_type, rhs_type, Opcodes.FADD, Opcodes.IADD);


    }

    @Override
    public void visitMinus(MinusOperation minus) {
        Expression lhs = minus.getLhs();
        Expression rhs = minus.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        codegenSides(lhs, rhs);

        asmHelper.performStackOp(lhs_type, rhs_type, Opcodes.FSUB, Opcodes.ISUB);

    }

    @Override
    public void visitModulo(ModuloOperation modulo) {
        Expression lhs = modulo.getLhs();
        Expression rhs = modulo.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        codegenSides(lhs, rhs);

        asmHelper.performStackOp(lhs_type, rhs_type, Opcodes.FREM, Opcodes.IREM);

    }

    // For booleans
    @Override
    public void visitUNegate(UnaryNegateOperation uNegate) {
        Expression lhs = uNegate.getLhs();
        assert lhs == null;
        Expression rhs = uNegate.getRhs();


        rhs.codeGen(this);

        asmHelper.pushIConst1();
        asmHelper.performSingleOp(Opcodes.IXOR);
    }

    @Override
    public void visitUMinus(UnaryMinusOperation uMinus) {
        Expression lhs = uMinus.getLhs();
        assert lhs == null;
        Expression rhs = uMinus.getRhs();
        GenericType rhs_type = rhs.getType();

        rhs.codeGen(this);
        asmHelper.performUMinus(rhs_type);
    }

    @Override
    public void visitMul(MultiplyOperation mul) {
        Expression lhs = mul.getLhs();
        Expression rhs = mul.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        codegenSides(lhs, rhs);

        asmHelper.performStackOp(lhs_type, rhs_type, Opcodes.FMUL, Opcodes.IMUL);
    }

    @Override
    public void visitDivide(DivideOperation div) {
        Expression lhs = div.getLhs();
        Expression rhs = div.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        codegenSides(lhs, rhs);

        asmHelper.performStackOp(lhs_type, rhs_type, Opcodes.FDIV, Opcodes.IDIV);
    }

    @Override
    public void visitAND(LogicalAnd and) {
        Expression lhs = and.getLhs();
        Expression rhs = and.getRhs();

        lhs.codeGen(this);
        rhs.codeGen(this);

        asmHelper.performSingleOp(Opcodes.IAND);
    }

    @Override
    public void visitOR(LogicalOr or) {
        Expression lhs = or.getLhs();
        Expression rhs = or.getRhs();

        lhs.codeGen(this);
        rhs.codeGen(this);

        asmHelper.performSingleOp(Opcodes.IOR);
    }

    @Override
    public void visitEquality(EqualComparison comp) {
        Expression lhs = comp.getLhs();
        Expression rhs = comp.getRhs();

        lhs.codeGen(this);
        rhs.codeGen(this);

        asmHelper.performComparison(Opcodes.IF_ICMPEQ);
    }

    @Override
    public void visitNotEqual(NotEqualComparison comp) {
        Expression lhs = comp.getLhs();
        Expression rhs = comp.getRhs();


        lhs.codeGen(this);
        rhs.codeGen(this);

        asmHelper.performComparison(Opcodes.IF_ICMPNE);
    }

    @Override
    public void visitGE(GEComparison ge) {
        Expression lhs = ge.getLhs();
        Expression rhs = ge.getRhs();

        lhs.codeGen(this);
        rhs.codeGen(this);

        asmHelper.performComparison(Opcodes.IF_ICMPGE);
    }

    @Override
    public void visitGT(GTComparison gt) {
        Expression lhs = gt.getLhs();
        Expression rhs = gt.getRhs();


        lhs.codeGen(this);
        rhs.codeGen(this);

        asmHelper.performComparison(Opcodes.IF_ICMPGT);

    }

    @Override
    public void visitLE(LEComparison le) {
        Expression lhs = le.getLhs();
        Expression rhs = le.getRhs();

        lhs.codeGen(this);
        rhs.codeGen(this);

        asmHelper.performComparison(Opcodes.IF_ICMPLE);
    }

    @Override
    public void visitLT(LTComparison lt) {
        Expression lhs = lt.getLhs();
        Expression rhs = lt.getRhs();

        lhs.codeGen(this);
        rhs.codeGen(this);

        asmHelper.performComparison(Opcodes.IF_ICMPLT);
    }

    @Override
    public void visitIfElse(IfElseStatement ifElseStatement) {

        // SCOPE
        asmHelper.startNewScope();

        Block ifBlock = ifElseStatement.getIfBlock();
        Block elseBlock = ifElseStatement.getElseBlock();
        Expression condition = ifElseStatement.getIfCondition();
        condition.codeGen(this);
        Label elseLabel = asmHelper.getLabel();
        asmHelper.performJumpOp(Opcodes.IFEQ, elseLabel);
        for(Statement s: ifBlock.getStatements()){
            s.codeGen(this);
        }
        Label endLabel = asmHelper.getLabel();
        asmHelper.performJumpOp(Opcodes.GOTO, endLabel);
        asmHelper.visitLabel(elseLabel);
        if(elseBlock != null){
            for(Statement s: elseBlock.getStatements()){
                s.codeGen(this);
            }
        }
        asmHelper.visitLabel(endLabel);
        asmHelper.popScope();
    }

    @Override
    public void visitFor(ForStatement forStatement) {
        asmHelper.startNewScope();
        Block forBlock = forStatement.getForBlock();
        Statement pos0 = forStatement.getPos0();
        Expression pos1 = forStatement.getPos1();
        Statement pos2 = forStatement.getPos2();


        // Step 1: Initialize loop counter
        //  - loop counter is initialized before so it is in the table
        //  - pos0 is going to initialize the loop counter (e.g., i = 1)
        pos0.codeGen(this);

        // Step 2: Start of the loop
        Label startLabel = asmHelper.getLabel();
        asmHelper.visitLabel(startLabel);

        // Step 3: Generate loop condition
        //  - pos1 is the condition (e.g., i < 10)
        //  - load loop variable
        pos1.codeGen(this);
        Label endLabel = asmHelper.getLabel();
        asmHelper.setCurrLoopEnd(endLabel);
        asmHelper.performJumpOp(Opcodes.IFEQ, endLabel);

        // Step 4: inside the loop
        for(Statement s: forBlock.getStatements()){
            s.codeGen(this);
        }

        // Step 5: pos2 contains the increment
        pos2.codeGen(this);

        // Step 6: jump back to the start
        asmHelper.performJumpOp(Opcodes.GOTO,startLabel);

        // Step 7: end label
        asmHelper.visitLabel(endLabel);
        asmHelper.popScope();
        asmHelper.setCurrLoopEnd(null);

    }

    @Override
    public void visitWhile(WhileStatement whileStatement) {
        asmHelper.startNewScope();
        Block whileBlock = whileStatement.getWhileBlock();
        Expression condition = whileStatement.getWhileCondition();

        // Step 1: Start of the loop
        Label startLabel = asmHelper.getLabel();
        asmHelper.visitLabel(startLabel);

        // Step 2: Generate loop condition
        //  - pos1 is the condition (e.g., i < 10)
        //  - load loop variable
        condition.codeGen(this);
        Label endLabel = asmHelper.getLabel();
        asmHelper.setCurrLoopEnd(endLabel);
        asmHelper.performJumpOp(Opcodes.IFEQ, endLabel);

        // Step 3: inside the loop
        for(Statement s: whileBlock.getStatements()){
            s.codeGen(this);
        }

        // Step 4: jump back to the start
        asmHelper.performJumpOp(Opcodes.GOTO,startLabel);

        // Step 5: end label
        asmHelper.visitLabel(endLabel);
        asmHelper.popScope();
        asmHelper.setCurrLoopEnd(null);

    }

    @Override
    public void visitStructDeclaration(StructDeclaration declaration) {
        Expression identifier = declaration.getStructIdentifier();
        Block structBlock = declaration.getStructBlock();
        // start the class writer for the struct
        asmHelper.startClassWriter(identifier.getRep());

        // go through the fields of the struct stored in the block of declaration
        StringBuilder sig = new StringBuilder();
        sig.append("(");
        for(Statement field : structBlock.getStatements()){
            field.codeGen(this);
            String t = field.getType().type();
            if(field.getType().isArray()){
               sig.append("[");
            }
            sig.append(asmHelper.getSignature(t,null));
        }
        sig.append(")V");

        HashMap<String, GenericType> fields = structFields.get(identifier.getRep());

        asmHelper.writeClass(identifier.getRep(), String.valueOf(sig), fields);


    }

    @Override
    public void visitDotOp(DotOperation op) {
        Expression rhs = op.getRhs();
        Expression lhs = op.getLhs();
        lhs.codeGen(this);
        GenericType t = structFields.get(lhs.getType().type()).get(rhs.getRep());
        String signature = asmHelper.getSignature(
                t.type(), null);

        if(t.isArray()){
            signature = "["+signature;
        }
        asmHelper.getFieldOfStruct(lhs.getType().type(), rhs.getRep(), signature);
    }

    public void readInt(){
        asmHelper.getUserInput();
        asmHelper.turnStrToInt();
    }

    public void readFloat(){
        asmHelper.getUserInput();
        asmHelper.turnStrToFloat();
    }

    public void readString(){
        asmHelper.getUserInput();
    }
    @Override
    public void visitFunctionCall(FunctionCallExpression functionCallExpression) {
        Symbol identifier = functionCallExpression.getFunctionIdentifier();
        var params = functionCallExpression.getFunctionExpressionParams();

        if(procedureInfos.containsKey(identifier.image())){
            switch (identifier.image()){
                case "readInt":
                    readInt();
                    return;
                case "readString":
                    readString();
                    return;
                case "readFloat":
                    readFloat();
                    return;
                case "writeInt":
                    writeInt(params.get(0));
                    return;
                case "writeFloat":
                    writeFloat(params.get(0));
                    return;
                case "write":
                    writeString(params.get(0));
                    return;
                case "writeln":
                    writeln();
                    return;
                case "len":
                    len(params.get(0));
                    return;
                case "chr":
                    chr(params.get(0));
                    return;
                case "floor":
                    floor(params.get(0));
                    return;
            }
        }

        if(structs.containsKey(identifier.image())){
            asmHelper.createStructInstance(identifier.image());
            StringBuilder signature = new StringBuilder();
            signature.append("(");
            for(Expression p: params){
                // TODO: Problem: when struct init as paramter the type is not given correctly
                // Instead of Point it gives [int, int] with t.type()
               p.codeGen(this);
               GenericType t = p.getType();
               if(p instanceof FunctionCallExpression && t instanceof UserType){
                   t = new Type(((FunctionCallExpression) p).getFunctionIdentifier().image(), false);
               }
               String sig = asmHelper.getSignature(t.type(), null);
               if(t.isArray()){
                   sig = "[" + sig;
               }
               signature.append(sig);
            }
            signature.append(")V");
            // get the function signature from params
            asmHelper.constructStructInstance(identifier.image(),
                    String.valueOf(signature));
        }
        else{ // normal function call
            StringBuilder signature = new StringBuilder();
            signature.append("(");
            for(Expression p: params){
                p.codeGen(this);
                GenericType t = p.getType();
                String sig = asmHelper.getSignature(t.type(), null);
                if(t.isArray()){
                    sig = "[" + sig;
                }
                signature.append(sig);
            }
            signature.append(")");
            GenericType t = functionCallExpression.getType();
            String retSig = asmHelper.getSignature(t.type(), null);
            if(t.isArray()){
                retSig = "[" + retSig;
            }
            signature.append(retSig);
            asmHelper.performFunctionCall(identifier.image(), String.valueOf(signature));


        }

    }


    void writeInt(Expression param){
        asmHelper.setPrintStream();
        param.codeGen(this);
        asmHelper.printInt();
    }
    void writeFloat(Expression param){
        asmHelper.setPrintStream();
        param.codeGen(this);
        asmHelper.printFloat();
    }

    void writeString(Expression param){
        asmHelper.setPrintStream();
        param.codeGen(this);
        asmHelper.printString();
    }

    void writeln(){
        asmHelper.setPrintStream();
        asmHelper.println();
    }

    void len(Expression param){
        param.codeGen(this);
        if(param.getType().type().equals("string") && !param.getType().isArray()){
            asmHelper.lengthStr();
        }else{
            asmHelper.lengthArr();
        }
    }

    void chr(Expression param){
        param.codeGen(this);
        asmHelper.turnIntToStr();
    }

    void floor(Expression param){
        param.codeGen(this);
        asmHelper.floorFloat();
    }



    private void addBuiltInFunctions(){
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

}
