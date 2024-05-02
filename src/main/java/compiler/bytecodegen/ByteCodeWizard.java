package compiler.bytecodegen;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.*;
import compiler.Parser.expressions.*;
import compiler.Parser.statements.*;
import compiler.semantics.ProcedureInfo;
import compiler.semantics.Type;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.exit;

// TODO: PROMOTING DOES NOT WORK => look at I2F opcode
// TODO: LOCAL VARIABLES
// TODO: New functions, signature creation based on parameters
// TODO: IF ELSE THINGS
// TODO: STRUCTS


public class ByteCodeWizard implements ByteVisitor{

    private Program program;
    private String programName;
    private Boolean localVariable = false;


    HashMap<String, ArrayList<ProcedureInfo>> procedureInfos;
    HashMap<String, String> constants;
    HashMap<String, String> globals;


    ASMHelper asmHelper;

    public ByteCodeWizard(Program program){
        this.program = program;
        this.programName = program.getFileName().replace(".lang","");
        this.procedureInfos = new HashMap<>();
        this.constants = new HashMap<>();
        this.globals = new HashMap<>();
        addBuiltInFunctions();
        for(ConstantVariable constantVariable: program.constantVariables){
            constants.put(constantVariable.getVariableName(), constantVariable.typeDeclaration().getTypeSymbol().image());
        }
        for(VariableGod variable: program.globals){
            globals.put(variable.getVariableName(), variable.typeDeclaration().getTypeSymbol().image());
        }

        asmHelper = new ASMHelper(programName, constants, globals);



    }

    public void codeGen(){
        program.codeGen(this);
    }

    public void visitProgram(Program program){
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

        // TODO: BIG FUCKING TODO
        // Generate a class for each struct type

        // Go through all the procedures
        this.localVariable=true;

        for(Procedure procedure: program.procedures){
            procedure.codeGen(this);
        }

        asmHelper.writeClass();
    }

    @Override
    public void visitProcedure(Procedure procedure) {


        asmHelper.startProcedureMethod(procedure.getProcedureIdentifier().image());

        asmHelper.startNewScope();

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
    public void visitConstantVariable(ConstantVariable variable) {
        String signature = asmHelper.getTypeSignatureStatic(variable);
        String name = variable.getVariableName();
        Expression declarator = variable.declarator();
        asmHelper.visitConstantVariable(name,signature);
        declarator.codeGen(this);
        asmHelper.storeStaticField(name,signature);
    }

    @Override
    public void visitScopeVariable(ScopeVariable variable) {
        boolean arr = false;

        Expression declarator = variable.getScopeDeclarator();
        Expression identifier = variable.getScopeIdentifier();
        String type = identifier.getType().type();

        String name = identifier.getRep();

        if(identifier instanceof IndexOp){
            prepIndexOp((IndexOp) identifier);
            arr = true;
            int index = name.indexOf('[');
            name = name.substring(0, index);
        }

        if(constants.containsKey(name)){
            final String ANSI_RESET = "\u001B[0m";
            final String ANSI_RED = "\u001B[31m";
            System.err.print(ANSI_RED);
            System.err.printf("ConstantError: --> Cannot modify constants at line %d", identifier.getLine());
            System.err.print(ANSI_RESET);
            exit(1);
        }
        declarator.codeGen(this);

        if(localVariable){
            asmHelper.updateLocalVariable(name);
        }else{
            String signature = asmHelper.getSignature(type, declarator);
            asmHelper.storeStaticField(name, signature);
        }
    }


    @Override
    public void visitIdentifier(IdentifierExpression expression) {
        String name = expression.getIdentifierSymbol().image();
        String type = null;
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
            asmHelper.getStaticField(name, type);
        }else{
            asmHelper.loadLocalVariable(name);
        }
    }


    @Override
    public void visitFunctionCall(FunctionCallExpression functionCallExpression) {
        Symbol identifier = functionCallExpression.getFunctionIdentifier();
        var params = functionCallExpression.getFunctionExpressionParams();

        if(procedureInfos.containsKey(identifier.image())){
            switch (identifier.image()){
                case "writeInt":
                    writeInt(params.get(0));
                    return;
                case "writeFloat":
                    writeFloat(params.get(0));
                    return;
                case "write":
                    writeString(params.get(0));
                    return;
            }
        }
        if(params.isEmpty()){
            // TODO: this is not only for empty things add parameter info as well
            System.out.println("IMPLEMENT FUNCTION CALL EXPRESSION VISIT");
            exit(1);
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
        // TODO: UPDATE FOR LOCALS
        Expression index  = op.getIndex();
        GenericType type = op.getType();
        Symbol identifier = op.getIndexIdentifier();
        if(localVariable){
            asmHelper.loadLocalVariable(identifier.image());
        }else{
            asmHelper.getStaticFieldArray(identifier.image(), type.type());
        }
        index.codeGen(this);
        switch (type.type()){
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
        }

    }
    public void prepIndexOp(IndexOp op){
        // TODO: UPDATE FOR LOCALS
        Expression index  = op.getIndex();
        GenericType type = op.getType();
        Symbol identifier = op.getIndexIdentifier();
        if(localVariable){
            asmHelper.loadLocalVariable(identifier.image());
        }else{
            asmHelper.getStaticFieldArray(identifier.image(), type.type());
        }
        index.codeGen(this);
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

        asmHelper.pushIntConst1();

        rhs.codeGen(this);

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
