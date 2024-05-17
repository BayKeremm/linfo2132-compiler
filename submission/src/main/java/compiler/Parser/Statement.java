package compiler.Parser;

import compiler.bytecodegen.ByteCodeGenerator;

public abstract class Statement extends ASTNode implements StatementChecker, ByteCodeGenerator {

    int line;
    protected Statement(int line) {
        this.line = line;
    }
    public abstract void prettyPrint(String indentation);

    public GenericType getType(){
        return null;
    }

    public String getVariableName(){
        return "Not a variable...";
    }

    public int getLine() {
        return line;
    }
}

