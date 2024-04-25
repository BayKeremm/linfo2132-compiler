package compiler.Parser;

import compiler.semantics.TypeVisitor;

public class ConstantVariable extends VariableGod {
    TypeDeclaration typeDecl;
    Expression identifier;
    Expression declarator;
    public ConstantVariable(int line, TypeDeclaration typeDecl, Expression identifier, Expression declarator) {
        super(line);
        this.declarator = declarator;
        this.typeDecl = typeDecl;
        this.identifier = identifier;
    }

    @Override
    public String getVariableName() {
        return identifier().getRep();
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print("Constant variable: \n");
        System.out.printf(indentation+"- type: %s\n", typeDecl.toString());
        System.out.printf(indentation+"- identifier: %s\n", identifier.toString());
        System.out.print(indentation+"- declaration:\n");
        declarator.prettyPrint(indentation+"  ");
        //System.out.printf(indentation+"- declarator: %s\n", declarator.toString());
    }

    @Override
    public boolean equals(Object o) {
        ConstantVariable c = (ConstantVariable) o;

        if(!this.typeDecl.equals(c.typeDecl)) return false;
        else if(!this.identifier.equals(c.identifier)) return false;
        else if(!this.declarator.equals(c.declarator)) return false;
        
        return true;
    }


    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitConstantVariable(this);
    }

    @Override
    public TypeDeclaration typeDeclaration() {
        return this.typeDecl;
    }

    @Override
    public Expression identifier() {
        return this.identifier;
    }

    @Override
    public Expression declarator() {
        return this.declarator;
    }
}
