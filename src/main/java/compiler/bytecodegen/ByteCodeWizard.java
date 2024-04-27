package compiler.bytecodegen;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.*;
import compiler.Parser.expressions.*;
import compiler.Parser.statements.*;
import compiler.semantics.ProcedureInfo;
import compiler.semantics.SemanticAnalysis;
import compiler.semantics.Type;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.exit;

/**
- TODO:
 - Constants
 - Globals
 - Structs
 **/
// TODO: PROMOTING DOES NOT WORK => look at I2F opcode


public class ByteCodeWizard implements ByteVisitor{

    private Program program;
    private String programName;
    private ClassWriter classWriter;
    private MethodVisitor currMethodVisitor;
    private MethodVisitor staticInitializer;
    HashMap<String, ArrayList<ProcedureInfo>> procedureInfos;
    HashMap<String, String> constants;
    HashMap<String, String> globals;

    public ByteCodeWizard(Program program){
        this.program = program;
        this.programName = program.getFileName().replace(".lang","");
        this.currMethodVisitor = null;
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
    }

    public void codeGen(){
        program.codeGen(this);
    }

    public void visitProgram(Program program){
        var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // Create a class named after the file we are compiling
        // we will put the procedures inside this class as methods
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, programName, null, "java/lang/Object", null);

        this.classWriter = cw;

        // Go through constants and add them to a hash table
        // put them as final static of the class

        for(ConstantVariable constantVariable: program.constantVariables){
            constantVariable.codeGen(this);
        }
        for(VariableGod var: program.globals){
            var.codeGen(this);
        }
        staticInitializer.visitInsn(Opcodes.RETURN);
        staticInitializer.visitMaxs(-1, -1);
        staticInitializer.visitEnd();


        // Go through globals and add them to a hash table
        // put them as statics of the class

        // Generate a class for each struct type

        // Go through all the procedures
        for(Procedure procedure: program.procedures){
            procedure.codeGen(this);
        }


        // write class file
        cw.visitEnd();

        var bytes = cw.toByteArray();
        try(var outFile = new FileOutputStream(programName+".class")) {
            outFile.write(bytes);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visitProcedure(Procedure procedure) {
        ArrayList<Statement> statements = procedure.getProcedureDeclarator().
                getProDecBlock().getStatements();


        MethodVisitor mw;
        // Do the main signature like Java expects
        if(procedure.getProcedureIdentifier().image().equals("main")){
            mw = this.classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    procedure.getProcedureIdentifier().image(), "([Ljava/lang/String;)V", null, null);
        }else{
            // TODO: depending on parameters change the descriptor (build it depending on arguments)
            mw = this.classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    procedure.getProcedureIdentifier().image(), "()V", null, null);
        }
        mw.visitCode();

        this.currMethodVisitor = mw;

        for (Statement st : statements){
            st.codeGen(this);
        }

        mw.visitInsn(Opcodes.RETURN);
        mw.visitEnd();
        mw.visitMaxs(-1, -1);

        this.currMethodVisitor = null;
    }

    @Override
    public void visitArrayInit(ArrayInitializer init) {
        GenericType type = init.getType();
        Expression size = init.getSize();
        size.codeGen(this);
        switch (type.type()){
            case "int":
                this.currMethodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
                break;
            case "float":
                this.currMethodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_FLOAT);
                break;
            case "string":
                this.currMethodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
                break;
            case "bool":
                this.currMethodVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN);
                break;
        }
    }

    @Override
    public void visitIndexOp(IndexOp op) {
        Expression index  = op.getIndex();
        GenericType type = op.getType();
        Symbol identifier = op.getIndexIdentifier();
        switch (type.type()){
            case "int":
                currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName,
                        identifier.image(), "[I");
                break;
            case "float":
                currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName,
                        identifier.image(), "[F");
                break;
            case "string":
                currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName,
                        identifier.image(), "[Ljava/lang/String;");
                break;
            case "bool":
                currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName,
                        identifier.image(), "[Z");
                break;
        }
        index.codeGen(this);
        switch (type.type()){
            case "int":
                currMethodVisitor.visitInsn(Opcodes.IALOAD);
                break;
            case "float":
                currMethodVisitor.visitInsn(Opcodes.FALOAD);
                break;
            case "string":
                currMethodVisitor.visitInsn(Opcodes.AALOAD);
                break;
            case "bool":
                currMethodVisitor.visitInsn(Opcodes.BALOAD);
                break;
        }
    }
    public void prepIndexOp(IndexOp op){
        Expression index  = op.getIndex();
        GenericType type = op.getType();
        Symbol identifier = op.getIndexIdentifier();
        switch (type.type()){
            case "int":
                currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName,
                        identifier.image(), "[I");
                break;
            case "float":
                currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName,
                        identifier.image(), "[F");
                break;
            case "string":
                currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName,
                        identifier.image(), "[Ljava/lang/String;");
                break;
            case "bool":
                currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName,
                        identifier.image(), "[Z");
                break;
        }
        index.codeGen(this);
    }

    public void visitLiteral(LiteralExpression exp){
        String lit = exp.getLiteral().image();
        int num;
        // Int
        try {
            num = Integer.parseInt(lit);
            this.currMethodVisitor.visitLdcInsn(num);
            return;
        } catch (NumberFormatException e) {
            try {
                float floatValue = Float.parseFloat(lit);
                this.currMethodVisitor.visitLdcInsn(floatValue);
                return;
            } catch (NumberFormatException ex) {
                System.out.println("VisitLiteral: The string does not contain a valid integer or float.");
            }
        }

        if(lit.equals("true") || lit.equals("false")){
            switch (lit){
                case "true":
                    this.currMethodVisitor.visitLdcInsn(true);
                    return;
                case "false":
                    this.currMethodVisitor.visitLdcInsn(false);
                    return;
            }
        }

        // String and true false
        this.currMethodVisitor.visitLdcInsn(lit);

    }

    @Override
    public void visitPlus(PlusOperation plus) {
        Expression lhs = plus.getLhs();
        Expression rhs = plus.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        lhs.codeGen(this);
        rhs.codeGen(this);


        if(lhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.IADD);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FADD);
        }
    }

    @Override
    public void visitMinus(MinusOperation minus) {
        Expression lhs = minus.getLhs();
        Expression rhs = minus.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        lhs.codeGen(this);
        rhs.codeGen(this);


        if(lhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.ISUB);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FSUB);
        }

    }

    @Override
    public void visitModulo(ModuloOperation modulo) {
        Expression lhs = modulo.getLhs();
        Expression rhs = modulo.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        lhs.codeGen(this);
        rhs.codeGen(this);


        if(lhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.IREM);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FREM);
        }

    }

    @Override
    public void visitUNegate(UnaryNegateOperation uNegate) {
        Expression lhs = uNegate.getLhs();
        assert lhs == null;
        Expression rhs = uNegate.getRhs();

        this.currMethodVisitor.visitLdcInsn(Opcodes.ICONST_1);
        rhs.codeGen(this);


        this.currMethodVisitor.visitInsn(Opcodes.IXOR);

    }

    @Override
    public void visitUMinus(UnaryMinusOperation uMinus) {
        Expression lhs = uMinus.getLhs();
        assert lhs == null;
        Expression rhs = uMinus.getRhs();
        GenericType rhs_type = rhs.getType();

        rhs.codeGen(this);
        if(rhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.INEG);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FNEG);
        }
    }

    @Override
    public void visitMul(MultiplyOperation mul) {
        Expression lhs = mul.getLhs();
        Expression rhs = mul.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        lhs.codeGen(this);
        rhs.codeGen(this);


        if(lhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.IMUL);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FMUL);
        }

    }

    @Override
    public void visitDivide(DivideOperation div) {
        Expression lhs = div.getLhs();
        Expression rhs = div.getRhs();
        GenericType lhs_type = lhs.getType();
        GenericType rhs_type = rhs.getType();

        lhs.codeGen(this);
        rhs.codeGen(this);


        if(lhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.IDIV);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FDIV);
        }
    }

    @Override
    public void visitAND(LogicalAnd and) {
        Expression lhs = and.getLhs();
        Expression rhs = and.getRhs();

        lhs.codeGen(this);
        rhs.codeGen(this);

        this.currMethodVisitor.visitInsn(Opcodes.IAND);

    }

    @Override
    public void visitOR(LogicalOr or) {
        Expression lhs = or.getLhs();
        Expression rhs = or.getRhs();

        lhs.codeGen(this);
        rhs.codeGen(this);

        this.currMethodVisitor.visitInsn(Opcodes.IOR);
    }

    @Override
    public void visitEquality(EqualComparison comp) {
        Expression lhs = comp.getLhs();
        Expression rhs = comp.getRhs();

        Label equalLabel = new Label();
        Label endLabel = new Label();

        lhs.codeGen(this);
        rhs.codeGen(this);

        this.currMethodVisitor.visitJumpInsn(Opcodes.IF_ICMPEQ, equalLabel);
        // If the comparison fails (lhs < rhs), push 0 (false) onto the stack
        this.currMethodVisitor.visitInsn(Opcodes.ICONST_0);
        this.currMethodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);

        this.currMethodVisitor.visitLabel(equalLabel);

        this.currMethodVisitor.visitInsn(Opcodes.ICONST_1);

        this.currMethodVisitor.visitLabel(endLabel);

    }

    @Override
    public void visitNotEqual(NotEqualComparison comp) {
        Expression lhs = comp.getLhs();
        Expression rhs = comp.getRhs();

        Label nEqualLabel = new Label();
        Label endLabel = new Label();

        lhs.codeGen(this);
        rhs.codeGen(this);

        this.currMethodVisitor.visitJumpInsn(Opcodes.IF_ICMPNE, nEqualLabel);
        // If the comparison fails (lhs < rhs), push 0 (false) onto the stack
        this.currMethodVisitor.visitInsn(Opcodes.ICONST_0);
        this.currMethodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);

        this.currMethodVisitor.visitLabel(nEqualLabel);

        this.currMethodVisitor.visitInsn(Opcodes.ICONST_1);

        this.currMethodVisitor.visitLabel(endLabel);

    }

    @Override
    public void visitGE(GEComparison ge) {
        Expression lhs = ge.getLhs();
        Expression rhs = ge.getRhs();

        Label greaterThanOrEqualLabel = new Label();
        Label endLabel = new Label();

        lhs.codeGen(this);
        rhs.codeGen(this);

        this.currMethodVisitor.visitJumpInsn(Opcodes.IF_ICMPGE, greaterThanOrEqualLabel);
        // If the comparison fails (lhs < rhs), push 0 (false) onto the stack
        this.currMethodVisitor.visitInsn(Opcodes.ICONST_0);
        this.currMethodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);

        this.currMethodVisitor.visitLabel(greaterThanOrEqualLabel);

        this.currMethodVisitor.visitInsn(Opcodes.ICONST_1);

        this.currMethodVisitor.visitLabel(endLabel);

    }

    @Override
    public void visitGT(GTComparison gt) {
        Expression lhs = gt.getLhs();
        Expression rhs = gt.getRhs();

        Label greater = new Label();
        Label endLabel = new Label();

        lhs.codeGen(this);
        rhs.codeGen(this);

        this.currMethodVisitor.visitJumpInsn(Opcodes.IF_ICMPGT, greater);
        // If the comparison fails (lhs < rhs), push 0 (false) onto the stack
        this.currMethodVisitor.visitInsn(Opcodes.ICONST_0);
        this.currMethodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);

        this.currMethodVisitor.visitLabel(greater);

        this.currMethodVisitor.visitInsn(Opcodes.ICONST_1);

        this.currMethodVisitor.visitLabel(endLabel);

    }

    @Override
    public void visitLE(LEComparison le) {
        Expression lhs = le.getLhs();
        Expression rhs = le.getRhs();

        Label lessEqual = new Label();
        Label endLabel = new Label();

        lhs.codeGen(this);
        rhs.codeGen(this);

        this.currMethodVisitor.visitJumpInsn(Opcodes.IF_ICMPLE, lessEqual);
        // If the comparison fails (lhs < rhs), push 0 (false) onto the stack
        this.currMethodVisitor.visitInsn(Opcodes.ICONST_0);
        this.currMethodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);

        this.currMethodVisitor.visitLabel(lessEqual);

        this.currMethodVisitor.visitInsn(Opcodes.ICONST_1);

        this.currMethodVisitor.visitLabel(endLabel);

    }

    @Override
    public void visitLT(LTComparison lt) {
        Expression lhs = lt.getLhs();
        Expression rhs = lt.getRhs();

        Label less = new Label();
        Label endLabel = new Label();

        lhs.codeGen(this);
        rhs.codeGen(this);

        this.currMethodVisitor.visitJumpInsn(Opcodes.IF_ICMPLT, less);
        // If the comparison fails (lhs < rhs), push 0 (false) onto the stack
        this.currMethodVisitor.visitInsn(Opcodes.ICONST_0);
        this.currMethodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);

        this.currMethodVisitor.visitLabel(less);

        this.currMethodVisitor.visitInsn(Opcodes.ICONST_1);

        this.currMethodVisitor.visitLabel(endLabel);

    }

    private String getSignature(VariableGod variable){
        String name = variable.getVariableName();
        String type;
        if(constants.containsKey(name)){
            type = constants.get(name);
        }else {
            type= globals.get(name);
        }

        if(staticInitializer == null){
            this.staticInitializer = this.classWriter.visitMethod(Opcodes.ACC_STATIC,
                    "<clinit>", "()V", null, null);
            staticInitializer.visitCode();
        }
        Expression declarator = variable.declarator();
        this.currMethodVisitor = staticInitializer;
        String signature = "";
        switch (type){
            case "int":
                if(declarator instanceof ArrayInitializer){
                    signature = "[I";
                }else{
                    signature = "I";
                }
                break;
            case "float":
                if(declarator instanceof ArrayInitializer){
                    signature = "[F";
                }else{
                    signature = "F";
                }
                break;
            case "string":
                if(declarator instanceof ArrayInitializer){
                    signature = "[Ljava/lang/String;";
                }else{
                    signature = "Ljava/lang/String;";
                }
                break;
            case "bool":
                if(declarator instanceof ArrayInitializer){
                    signature = "[Z";
                }else{
                    signature = "Z";
                }
                break;
        }
        return signature;
    }

    @Override
    public void visitConstantVariable(ConstantVariable variable) {
        String name = variable.getVariableName();
        Expression declarator = variable.declarator();
        String signature = getSignature(variable);
        this.classWriter.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                name, signature, null, null).visitEnd();
        declarator.codeGen(this);
        staticInitializer.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, signature);
        this.currMethodVisitor = null;
    }

    @Override
    public void visitVariable(Variable variable) {
        String name = variable.getVariableName();
        Expression declarator = variable.declarator();
        String signature = getSignature(variable);
        this.classWriter.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                name, signature, null, null).visitEnd();
        declarator.codeGen(this);
        staticInitializer.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, signature);
        this.currMethodVisitor = null;
    }

    @Override
    public void visitScopeVariable(ScopeVariable variable) {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_RED = "\u001B[31m";
        Expression declarator = variable.getScopeDeclarator();
        Expression identifier = variable.getScopeIdentifier();
        String type = identifier.getType().type();
        boolean arr = false;

        String name = identifier.getRep();
        if(identifier instanceof IndexOp){
            prepIndexOp((IndexOp) identifier);
            arr = true;
            int index = name.indexOf('[');
            name = name.substring(0, index);
        }
        if(constants.containsKey(name)){
            System.err.print(ANSI_RED);
            System.err.printf("ConstantError: --> Cannot modify constants at line %d", identifier.getLine());
            System.err.print(ANSI_RESET);
            exit(1);
        }
        declarator.codeGen(this);

        if(arr){
            switch (type){
                case "int":
                    currMethodVisitor.visitInsn(Opcodes.IASTORE);
                    break;
                case "float":
                    currMethodVisitor.visitInsn(Opcodes.FASTORE);
                    break;
                case "string":
                    currMethodVisitor.visitInsn(Opcodes.AASTORE);
                    break;
            }

        }else {
            switch (type){
                case "int":
                    this.currMethodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, "I");
                    break;
                case "float":
                    this.currMethodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, "F");
                    break;
                case "string":
                    this.currMethodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, "Ljava/lang/String;");
                    break;
            }
        }

    }


    @Override
    public void visitIdentifier(IdentifierExpression expression) {
        String name = expression.getIdentifierSymbol().image();
        String type = null;
        if(constants.containsKey(name) ){
            type =  constants.get(name);
        }else if(globals.containsKey(name)){
            type =  globals.get(name);
        }else{
            System.err.println("PROBLEM IN VISITIDENTIFIER IN BYTECODEGEN");
            exit(1);
        }
        // Constant
            switch(type){
                case "int":
                    this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName, name, "I");
                    return;
                case "float":
                    this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName, name, "F");
                    return;
                case "string":
                    this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, programName, name, "Ljava/lang/String;");
                    return;
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
            this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, programName, identifier.image(), "()V", false);
        }

    }

    void writeInt(Expression param){
        this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System", "out", "Ljava/io/PrintStream;");
        param.codeGen(this);
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
    }
    void writeFloat(Expression param){
        this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System", "out", "Ljava/io/PrintStream;");
        param.codeGen(this);
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
    }

    void writeString(Expression param){
        this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System", "out", "Ljava/io/PrintStream;");
        param.codeGen(this);
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
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
