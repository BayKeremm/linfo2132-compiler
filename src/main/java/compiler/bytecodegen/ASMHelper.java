package compiler.bytecodegen;

import compiler.Parser.GenericType;
import compiler.Parser.expressions.ArrayInitializer;
import compiler.Parser.expressions.Expression;
import compiler.Parser.expressions.FunctionCallExpression;
import compiler.Parser.statements.StructDeclaration;
import compiler.Parser.statements.VariableGod;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ASMHelper {
    private final String className;

    HashMap<String, GenericType> constants;
    HashMap<String, GenericType> globals;
    HashMap<String, StructDeclaration> structDeclarations;
    LocalScope currentScope;

    ClassWriter classWriter;
    ClassWriter previousClassWriter;

    MethodVisitor currMethodVisitor;

    public ASMHelper(String className, HashMap<String, GenericType> constants,
                     HashMap<String, GenericType> globals, HashMap<String, StructDeclaration> structDeclarations) {
        this.className = className;
        this.constants = constants;
        this.globals = globals;
        this.structDeclarations = structDeclarations;
    }

    public void startClassWriter(){
        var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, className, null,
                "java/lang/Object", null);

        this.classWriter = cw;
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

    public void startClassWriter(String structName){
        var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, structName, null,
                "java/lang/Object", null);

        this.classWriter = cw;
    }
    // Only for structs
    public void writeClass(String structName, String signature, HashMap<String, GenericType> fields){
        // Generate constructor for the struct class
        MethodVisitor constructor = classWriter.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>", signature, null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this'
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        int index = 1;
        for (Map.Entry<String, GenericType> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            GenericType fieldType = entry.getValue();
            constructor.visitVarInsn(Opcodes.ALOAD, 0); // Load 'this'
            String sig = getSignature(fieldType.type(), null);
            if(fieldType.isArray() ){
                sig = "["+sig;
                constructor.visitVarInsn(Opcodes.ALOAD, index);

            }else{
                switch (fieldType.type()){
                    case "int", "bool":
                        constructor.visitVarInsn(Opcodes.ILOAD, index);
                        break;
                    case "float":
                        constructor.visitVarInsn(Opcodes.FLOAD, index);
                        break;
                    default:
                        constructor.visitVarInsn(Opcodes.ALOAD, index);
                        break;
                }
            }
            constructor.visitFieldInsn(Opcodes.PUTFIELD, structName, fieldName, sig);
            index++;
        }

        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(-1, -1);
        constructor.visitEnd();

        // write class file
        classWriter.visitEnd();

        var bytes = classWriter.toByteArray();
        try(var outFile = new FileOutputStream(structName+".class")) {
            outFile.write(bytes);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        this.classWriter = null;

    }

    public void getFieldOfStruct(String userType, String field, String signature){
        currMethodVisitor.visitFieldInsn(Opcodes.GETFIELD,
                userType, field, signature);
    }


    public void startNewScope(){
        // init new local scope
        LocalScope newScope = new LocalScope();
        newScope.setPrevScope(currentScope);
        int index = 0;
        if(currentScope != null){
            index = currentScope.getCurrIndex();
        }
        currentScope = newScope;
        currentScope.setStartIndex(index);
        //currMethodVisitor.visitLabel(currentScope.getStartLabel());
        //currentScope.setStartVisit(true);
    }
    public void popScope(){
        //if(!currentScope.isEndVisit()){
        //    currMethodVisitor.visitLabel(currentScope.getEndLabel());
        //    currentScope.setEndVisit(true);
        //}
        currentScope = currentScope.getPrevScope();
    }

    public void addLocalToScope(String name, GenericType type){
        currentScope.addToTable(name,type);
    }

    public void startProcedureMethod(String procedureName, String  signature){
        MethodVisitor mw;
        // Do the main signature like Java expects
        if(procedureName.equals("main")){
            mw = this.classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    "main", "([Ljava/lang/String;)V", null, null);
        }else{
            mw = this.classWriter.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                    procedureName, signature, null, null);
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

    public void getStaticField(String name, String signature){
        this.currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC, className, name, signature);
    }

    public void visitLocalVariable(String name, String signature ){
        currMethodVisitor.visitLocalVariable(name, signature,
                null, currentScope.getStartLabel(), currentScope.getEndLabel(),currentScope.getIndex(name) );
    }
    public void putStructField(String name, String fieldName, GenericType type){
        String sig = getSignature(type.type(), null);
        if(type.isArray()){
            sig = "[" + sig;
        }
        currMethodVisitor.visitFieldInsn(Opcodes.PUTFIELD, name, fieldName, sig);
    }

    public void loadStruct(String name){
        int index = currentScope.getIndex(name);
        this.currMethodVisitor.visitVarInsn(Opcodes.ALOAD, index);
    }

    public void updateLocalVariable(String name){
        int index = currentScope.getIndex(name);

        String type = currentScope.getType(name).type();
        boolean arr = currentScope.getType(name).isArray();

        switch(type){
            case "int":
                if(arr){
                    this.currMethodVisitor.visitInsn(Opcodes.IASTORE);
                }else{
                    this.currMethodVisitor.visitVarInsn(Opcodes.ISTORE, index);
                }
                break;
            case "float":
                if(arr){
                    this.currMethodVisitor.visitInsn(Opcodes.FASTORE);
                }else{
                    this.currMethodVisitor.visitVarInsn(Opcodes.FSTORE,index);
                }
                break;
            case "string":
                if(arr){
                    this.currMethodVisitor.visitInsn(Opcodes.AASTORE);
                }else{
                    this.currMethodVisitor.visitVarInsn(Opcodes.ASTORE, index);
                }
                break;
            case "bool":
                if(arr){
                    this.currMethodVisitor.visitInsn(Opcodes.BASTORE);
                }else{
                    this.currMethodVisitor.visitVarInsn(Opcodes.ISTORE, index);
                }
                break;
            default:
                // This is for stuff like:     int x = ps[0].x;
                this.currMethodVisitor.visitInsn(Opcodes.AASTORE);
                break;

        }

    }
    public void storeLocalVariable(String name){
        int index = currentScope.getIndex(name);

        GenericType t = currentScope.getType(name);
        String type = t.type();
        boolean arr = t.isArray();

        if(arr){
            this.currMethodVisitor.visitVarInsn(Opcodes.ASTORE,index );
        } else{
            switch(type){
                case "int", "bool":
                    this.currMethodVisitor.visitVarInsn(Opcodes.ISTORE,index );
                    break;
                case "float":
                    this.currMethodVisitor.visitVarInsn(Opcodes.FSTORE,index );
                    break;
                default: // for strings and structs
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
                default: // for strings and structs
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

    public void visitStructField(String name, String signature){
        this.classWriter.visitField(Opcodes.ACC_PUBLIC,
                name, signature, null, null).visitEnd();

    }

    public void createStructInstance(String name){
        currMethodVisitor.visitTypeInsn(Opcodes.NEW, name);
        currMethodVisitor.visitInsn(Opcodes.DUP);
    }

    public void constructStructInstance(String name, String signature){
        currMethodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                name, "<init>", signature, false);
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

    public void visitArrayInitStruct(String type){
        this.currMethodVisitor.visitTypeInsn(Opcodes.ANEWARRAY,type );
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
                    currMethodVisitor.visitInsn(Opcodes.ICONST_1);
                    return;
                case "false":
                    currMethodVisitor.visitInsn(Opcodes.ICONST_0);
                    return;
            }
        }

        literal = literal.replace("\"", "");
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

    public void performReturn(GenericType type){
        boolean arr = type.isArray();
        if(arr){
            this.currMethodVisitor.visitInsn(Opcodes.ARETURN);
        }else{
            switch(type.type()){
                case "int", "bool":
                    this.currMethodVisitor.visitInsn(Opcodes.IRETURN);
                    break;
                case "float":
                    this.currMethodVisitor.visitInsn(Opcodes.FRETURN);
                    break;
                case "string":
                    this.currMethodVisitor.visitInsn(Opcodes.ARETURN);
                    break;
                default:
                    // This is to return structs
                    this.currMethodVisitor.visitInsn(Opcodes.ARETURN);
                    break;

            }

        }

    }

    public void performFunctionCall(String name, String signature){
        currMethodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, className, name, signature, false); // Call square method
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
                "print", "(Ljava/lang/String;)V", false);
    }
    public void println(){
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",
                "println", "()V", false);
    }

    public void lengthStr(){
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
    }

    public void lengthArr(){
        this.currMethodVisitor.visitInsn(Opcodes.ARRAYLENGTH);
    }

    public void turnIntToStr(){
        this.currMethodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                "java/lang/String", "valueOf",
                "(I)Ljava/lang/String;", false);
    }

    public void turnStrToInt(){
        currMethodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
    }

    public void turnStrToFloat(){
        currMethodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                "java/lang/Float", "parseFloat", "(Ljava/lang/String;)F", false);
    }

    public void floorFloat(){
        this.currMethodVisitor.visitInsn(Opcodes.F2I);
    }

    public void getUserInput(){
        currMethodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        currMethodVisitor.visitInsn(Opcodes.DUP);
        currMethodVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System", "in", "Ljava/io/InputStream;");
        currMethodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
        currMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);

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
            default:
                signature = "L"+type+";";
        }
        return signature;
    }
    public String getTypeSignatureStatic(VariableGod variable){
        String name = variable.getVariableName();
        String type="";
        if(constants.containsKey(name)){
            type = constants.get(name).type();
        }else {
            type= globals.get(name).type();
        }
        Expression declarator = variable.declarator();
        return getSignature(type,declarator);
    }

    public String getLocalSignature(VariableGod variable){
        String typeName = "";
        if(variable.declarator() instanceof FunctionCallExpression){
           typeName = ((FunctionCallExpression) variable.declarator())
                   .getFunctionIdentifier().image();
        }
        if(structDeclarations.containsKey(typeName)){
            //return "L"+typeName;
            return "L"+typeName+";";
        }else{
            Expression declarator = variable.declarator();
            String type = declarator.getType().type();
            return getSignature(type,declarator);
        }
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
            default:
                signature = "L"+type+";";
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
