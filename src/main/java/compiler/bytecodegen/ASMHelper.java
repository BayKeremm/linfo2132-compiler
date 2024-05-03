package compiler.bytecodegen;

import compiler.Parser.GenericType;
import compiler.Parser.expressions.ArrayInitializer;
import compiler.Parser.expressions.Expression;
import compiler.Parser.statements.VariableGod;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ASMHelper {
    private final String className;

    HashMap<String, String> constants;
    HashMap<String, String> globals;
    LocalScope currentScope;

    ClassWriter classWriter;

    MethodVisitor currMethodVisitor;

    public ASMHelper(String className, HashMap<String, String> constants,
                     HashMap<String, String> globals) {
        this.className = className;
        this.constants = constants;
        this.globals = globals;
    }

    public void startNewScope(){
        // init new local scope
        LocalScope newScope = new LocalScope();
        newScope.setPrevScope(currentScope);
        currentScope = newScope;
        currMethodVisitor.visitLabel(currentScope.getStartLabel());
        currentScope.setStartVisit(true);
    }

    public void addLocalToScope(String name, GenericType type){
        currentScope.addToTable(name,type);

    }


    public void writeClass(){
        // write class file
        classWriter.visitEnd();

        var bytes = classWriter.toByteArray();
        try(var outFile = new FileOutputStream(className+".class")) {
            outFile.write(bytes);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

    public void startClassWriter(){
        /**
        *
        ClassWriter:visit
            Visits the header of the class.
        * */
        var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, className, null,
                "java/lang/Object", null);

        this.classWriter = cw;
    }

    public void startProcedureMethod(String procedureName){
        MethodVisitor mw;
        // Do the main signature like Java expects
        if(procedureName.equals("main")){
            mw = this.classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    "main", "([Ljava/lang/String;)V", null, null);
        }else{
            // TODO: depending on parameters change the descriptor (build it depending on arguments)
            mw = this.classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    procedureName, "()V", null, null);
        }
        mw.visitCode();

        this.currMethodVisitor = mw;

    }

    public void startStaticInitializer(){
       /**
    ClassWriter:visitMethod
        Visits a method of the class. This method must return a new MethodVisitor instance (or null)
            each time it is called, i.e., it should not return a previously returned visitor.
       * */
        currMethodVisitor = this.classWriter.visitMethod(Opcodes.ACC_STATIC,
                "<clinit>", "()V", null, null);
        /**
        MethodVisitor:visitCode
        Starts the visit of the method's code, if any (i.e. non abstract method).
        * */
        currMethodVisitor.visitCode();
    }

    public void popScope(){
        if(!currentScope.isEndVisit()){
            currMethodVisitor.visitLabel(currentScope.getEndLabel());
            currentScope.setEndVisit(true);
        }
        currentScope = currentScope.getPrevScope();
    }

    public void endMethodVisitor(){
        // Visits a zero operand instruction.
        currMethodVisitor.visitInsn(Opcodes.RETURN);
        // Visits the maximum stack size and the maximum number of local variables of the method.
        currMethodVisitor.visitMaxs(-1, -1);
        // Visits the end of the method. This method, which is the last one to be called,
        // is used to inform the visitor that all the annotations and attributes of the method have been visited.
        currMethodVisitor.visitEnd();

        currMethodVisitor = null;

    }

    public void storeStaticField(String name, String signature){
        /**
         Visits a field instruction. A field instruction is an instruction that loads or stores
            the value of a field of an object.
         */
        currMethodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, className, name, signature);
    }

    public void storeStaticFieldArray(String type){
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
    }

    public void getStaticField(String name, String type){
        switch(type){
            case "int":
                this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, "I");
                break;
            case "float":
                this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, "F");
                break;
            case "bool":
                this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, "Z");
                break;
            case "string":
                this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, "Ljava/lang/String;");
                break;
        }
    }

    public void getStaticFieldArray(String name, String type){
        switch(type){
            case "int":
                this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, "[I");
                break;
            case "float":
                this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, "[F");
                break;
            case "bool":
                this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, "[Z");
                break;
            case "string":
                this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, "[Ljava/lang/String;");
                break;
        }
    }

    public void visitLocalVariable(String name, String signature ){
        currMethodVisitor.visitLocalVariable(name, signature,
                null, currentScope.getStartLabel(), currentScope.getEndLabel(),currentScope.getIndex(name) );
    }

    public void updateLocalVariable(String name){
        int index = currentScope.getIndex(name);

        String type = currentScope.getType(name).type();
        boolean arr = currentScope.getType(name).isArray();

        switch(type){
            case "int", "bool":
                if(arr){
                    this.currMethodVisitor.visitInsn(Opcodes.IASTORE);
                }else{
                    this.currMethodVisitor.visitInsn(Opcodes.ISTORE);
                }
                break;
            case "float":
                if(arr){
                    this.currMethodVisitor.visitInsn(Opcodes.FASTORE);
                }else{
                    this.currMethodVisitor.visitInsn(Opcodes.FSTORE);
                }
                break;
            case "string":
                if(arr){
                    this.currMethodVisitor.visitInsn(Opcodes.AASTORE);
                }else{
                    this.currMethodVisitor.visitInsn(Opcodes.ASTORE);
                }
                break;
        }

    }
    public void storeLocalVariable(String name){
        int index = currentScope.getIndex(name);

        String type = currentScope.getType(name).type();
        boolean arr = currentScope.getType(name).isArray();

        if(arr){
            this.currMethodVisitor.visitVarInsn(Opcodes.ASTORE,index );
        }else{
            switch(type){
                case "int", "bool":
                    this.currMethodVisitor.visitVarInsn(Opcodes.ISTORE,index );
                    break;
                case "float":
                    this.currMethodVisitor.visitVarInsn(Opcodes.FSTORE,index );
                    break;
                case "string":
                    this.currMethodVisitor.visitVarInsn(Opcodes.ASTORE,index );
                    break;
            }
        }


    }


    public void loadLocalVariable(String name){
        String type = currentScope.getType(name).type();
        boolean arr = currentScope.getType(name).isArray();
        int index = currentScope.getIndex(name);

        if(arr) {
            this.currMethodVisitor.visitVarInsn(Opcodes.ALOAD, index);
        }else{
            switch(type){
                case "int", "bool":
                    this.currMethodVisitor.visitVarInsn(Opcodes.ILOAD, index);
                    break;
                case "float":
                    this.currMethodVisitor.visitVarInsn(Opcodes.FLOAD, index);
                    break;
                case "string":
                    this.currMethodVisitor.visitVarInsn(Opcodes.ALOAD, index);
                    break;
            }

        }

    }


    public void visitGlobalVariable(String name, String signature){
        this.classWriter.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                name, signature, null, null).visitEnd();
    }

    public void visitConstantVariable(String name, String signature){
        this.classWriter.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                name, signature, null, null).visitEnd();
    }

    public void visitArrayInit(String type){
        switch (type){
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

    public void pushLiteral(String literal){
        // Methodvisitor: visitLdcInsn: add constant to the stack
        try {
            int num = Integer.parseInt(literal);
            currMethodVisitor.visitLdcInsn(num);
            return;
        } catch (NumberFormatException e) {
            try {
                float floatValue = Float.parseFloat(literal);
                currMethodVisitor.visitLdcInsn(floatValue);
                return;
            } catch (NumberFormatException ex) {
                System.out.println("VisitLiteral: The string does not contain a valid integer or float.");
            }
        }
        if(literal.equals("true") || literal.equals("false")){
            switch (literal){
                case "true":
                    currMethodVisitor.visitLdcInsn(Opcodes.ICONST_1);
                    return;
                case "false":
                    currMethodVisitor.visitLdcInsn(Opcodes.ICONST_0);
                    return;
            }
        }

        currMethodVisitor.visitLdcInsn(literal);
    }

    public void pushIConst1(){
        this.currMethodVisitor.visitInsn(Opcodes.ICONST_1);
    }

    public void turnIntToFloat(){
        this.currMethodVisitor.visitInsn(Opcodes.I2F);
    }

    public void turnFloatToInt(){
        this.currMethodVisitor.visitInsn(Opcodes.F2I);
    }

    public void performSingleOp(Integer opcode){
        this.currMethodVisitor.visitInsn(opcode);
    }

    public Label getLabel(){
        return new Label();
    }

    public void performJumpOp(Integer opcode, Label label){
        this.currMethodVisitor.visitJumpInsn(opcode,label);
    }

    public void visitLabel(Label label){
       this.currMethodVisitor.visitLabel(label);
    }

    public void performComparison(Integer opcode){
        Label conditionLabel = new Label();
        Label endLabel = new Label();

        this.currMethodVisitor.visitJumpInsn(opcode, conditionLabel);
        // If the comparison fails (lhs < rhs), push 0 (false) onto the stack
        this.currMethodVisitor.visitInsn(Opcodes.ICONST_0);
        this.currMethodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);

        this.currMethodVisitor.visitLabel(conditionLabel);

        this.currMethodVisitor.visitInsn(Opcodes.ICONST_1);

        this.currMethodVisitor.visitLabel(endLabel);

    }

    public void performUMinus(GenericType rhs_type){
        if(rhs_type.type().equals("int")){
            this.currMethodVisitor.visitInsn(Opcodes.INEG);
        }else{
            this.currMethodVisitor.visitInsn(Opcodes.FNEG);
        }
    }

    public void performStackOp(GenericType lhs_t, GenericType rhs_t, Integer opcode1, Integer opcode2){
        if(lhs_t.type().equals("float") || rhs_t.type().equals("float")){
            this.currMethodVisitor.visitInsn(opcode1);
        }else{
            this.currMethodVisitor.visitInsn(opcode2);
        }
    }

    public void setPrintStream(){
        this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System", "out", "Ljava/io/PrintStream;");
    }
    public void printInt(){
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                "println", "(I)V", false);
    }

    public void printFloat(){
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                "println", "(F)V", false);
    }

    public void printString(){
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                "println", "(Ljava/lang/String;)V", false);
    }

    public String getSignature(String type, Expression declarator){
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
    public String getTypeSignatureStatic(VariableGod variable){
        String name = variable.getVariableName();
        String type="";
        if(constants.containsKey(name)){
            type = constants.get(name);
        }else {
            type= globals.get(name);
        }
        Expression declarator = variable.declarator();
        return getSignature(type,declarator);
    }

    public String getLocalSignature(VariableGod variable){
        Expression declarator = variable.declarator();
        String type = declarator.getType().type();
        return getSignature(type,declarator);
    }

    public String getUnInitSignature(String name, GenericType type){
        boolean arr = type.isArray();
        String signature = "";
        switch (type.type()){
            case "int":
                if(arr){
                    signature = "[I";
                }else{
                    signature = "I";
                }
                break;
            case "float":
                if(arr){
                    signature = "[F";
                }else{
                    signature = "F";
                }
                break;
            case "string":
                // TODO: EMPTY DEFAULT VAL ???
                if(arr){
                    signature = "[Ljava/lang/String;";
                }else{
                    signature = "Ljava/lang/String;";
                }
                break;
            case "bool":
                if(arr){
                    signature = "[Z";
                }else{
                    signature = "Z";
                }
                break;
        }
        return signature;
    }
    public void pushDefaultValue(String name){
        GenericType type = currentScope.getType(name);
        switch (type.type()){
            case "int", "bool":
                this.currMethodVisitor.visitInsn(Opcodes.ICONST_0);
                break;
            case "float":
                this.currMethodVisitor.visitInsn(Opcodes.FCONST_0);
                break;
            case "string":
                this.pushLiteral("null");
                break;
        }
    }
}
