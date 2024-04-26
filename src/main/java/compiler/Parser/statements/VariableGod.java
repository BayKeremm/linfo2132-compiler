package compiler.Parser.statements;

import compiler.Parser.expressions.Expression;
import compiler.Parser.statements.Statement;
import compiler.Parser.statements.TypeDeclaration;

public abstract class  VariableGod extends Statement {

    protected VariableGod(int line) {
        super(line);
    }
    public abstract TypeDeclaration typeDeclaration();
    public abstract Expression identifier();
    public abstract Expression declarator();
}
