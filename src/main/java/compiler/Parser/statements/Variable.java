package compiler.Parser.statements;

import compiler.Parser.expressions.Expression;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class Variable extends VariableGod {
    TypeDeclaration typeDeclaration;
    Expression identifier;
    Expression declarator;
    public Variable(int line, TypeDeclaration typeDeclaration, Expression identifier, Expression declarator) {
        super(line);
        this.declarator = declarator;
        this.typeDeclaration = typeDeclaration;
        this.identifier = identifier;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitVariable(this);
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.println(indentation+"Variable: ");
        System.out.printf(indentation+"  - type: %s\n", typeDeclaration.toString());
        System.out.printf(indentation+"  - identifier: %s\n", identifier.toString());
        //System.out.printf(indentation+"- declaration: %s\n", declarator.toString());
        System.out.println(indentation+"  - declaration:");
        declarator.prettyPrint(indentation+"    ");


    }

    @Override
    public String toString() {
        return "Variable{" +
                typeDeclaration +
                ", " + identifier +
                ", (" + declarator+
                ") }";
    }

    @Override
    public boolean equals(Object o) {
        Variable v = (Variable) o;

        if(!this.typeDeclaration.equals(v.typeDeclaration)) return false;
        else if(!this.declarator.equals(v.declarator)) return false;
        else if(!this.identifier.equals(v.identifier)) return false;
        
        return true;
    }


    @Override
    public TypeDeclaration typeDeclaration() {
        return this.typeDeclaration;
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
    public void codeGen(ByteVisitor b) {

    }
}
