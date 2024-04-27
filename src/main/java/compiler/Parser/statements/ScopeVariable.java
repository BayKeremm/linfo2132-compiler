package compiler.Parser.statements;

import compiler.Parser.expressions.Expression;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class ScopeVariable extends VariableGod {
    Expression identifier;

    public Expression getScopeIdentifier() {
        return identifier;
    }

    public Expression getScopeDeclarator() {
        return declarator;
    }

    Expression declarator;
    public ScopeVariable(int line, Expression identifier, Expression declarator) {
        super(line);
        this.declarator = declarator;
        this.identifier = identifier;
    }

    @Override
    public String getVariableName() {
        return identifier().getRep();
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Scope variable: \n");
        System.out.printf(indentation+"- identifier: %s\n", identifier.toString());
        System.out.printf(indentation+"- declaration: %s\n", declarator.toString());
    }

    @Override
    public String toString() {
        return "ScopeVar{ " +
                identifier +
                "," + "( "+ declarator + " )"+
                " }";
    }

    @Override
    public boolean equals(Object o) {
        ScopeVariable s = (ScopeVariable) o;

        if(!this.identifier.equals(s.identifier)) return false;
        else if(!this.declarator.equals(s.declarator)) return false;

        return true;
    }

    @Override
    public TypeDeclaration typeDeclaration() {
        return null;
    }

    @Override
    public Expression identifier() {
        return this.identifier;
    }

    @Override
    public Expression declarator() {
        return this.declarator;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitScopeVariable(this);

    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitScopeVariable(this);
    }
}
