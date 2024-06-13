package compiler.Parser;


public abstract class  VariableGod extends Statement {

    protected VariableGod(int line) {
        super(line);
    }
    public abstract TypeDeclaration typeDeclaration();
    public abstract Expression identifier();
    public abstract Expression declarator();
}
