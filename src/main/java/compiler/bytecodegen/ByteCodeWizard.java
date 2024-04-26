package compiler.bytecodegen;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
import compiler.Parser.*;
import compiler.Parser.expressions.*;
import compiler.Parser.statements.ConstantVariable;
import compiler.Parser.statements.Procedure;
import compiler.Parser.statements.Statement;
import compiler.semantics.ProcedureInfo;
import compiler.semantics.Type;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
- TODO:
 - Constants
 - Globals
 - Structs
 **/


public class ByteCodeWizard implements ByteVisitor{

    private Program program;
    private String programName;
    private ClassWriter classWriter;
    private MethodVisitor currMethodVisitor;
    private MethodVisitor staticInitializer;
    HashMap<String, ArrayList<ProcedureInfo>> procedureInfos;
    HashMap<String, String> constants;

    public ByteCodeWizard(Program program){
        this.program = program;
        this.programName = program.getFileName().replace(".lang","");
        this.currMethodVisitor = null;
        this.procedureInfos = new HashMap<>();
        this.constants = new HashMap<>();
        addBuiltInFunctions();
        for(ConstantVariable constantVariable: program.constantVariables){
            constants.put(constantVariable.getVariableName(), constantVariable.typeDeclaration().getTypeSymbol().image());
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


        // TODO: PROMOTING DOES NOT WORK
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


        // TODO: PROMOTING DOES NOT WORK
        if(lhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.ISUB);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FSUB);
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


        // TODO: PROMOTING DOES NOT WORK
        if(lhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.IMUL);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FMUL);
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
    public void visitConstantVariable(ConstantVariable variable) {
        String name = variable.getVariableName();
        String type = constants.get(name);
        Expression declarator = variable.declarator();
        if(staticInitializer == null){
            this.staticInitializer = this.classWriter.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            staticInitializer.visitCode();
        }
        this.currMethodVisitor = staticInitializer;
        switch (type){
            case "int":
                this.classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
                        name, "I", null, null).visitEnd();
                declarator.codeGen(this);
                staticInitializer.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, "I");
                break;
            case "float":
                this.classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
                        name, "F", null, null).visitEnd();
                declarator.codeGen(this);
                staticInitializer.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, "F");
                break;
            case "string":
                this.classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
                        name, "Ljava/lang/String;", null, null).visitEnd();
                declarator.codeGen(this);
                staticInitializer.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, "Ljava/lang/String;");
                break;
            case "bool":
                this.classWriter.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL,
                        name, "Z", null, null).visitEnd();
                declarator.codeGen(this);
                staticInitializer.visitFieldInsn(Opcodes.PUTSTATIC, programName, name, "Z");
                break;

        }
        this.currMethodVisitor = null;
    }

    @Override
    public void visitIdentifier(IdentifierExpression expression) {
        String name = expression.getIdentifierSymbol().image();
        String type =  constants.get(name);
        // Constant
        if(constants.containsKey(name)){
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
