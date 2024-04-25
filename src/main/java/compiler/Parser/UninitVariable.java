package compiler.Parser;

import compiler.semantics.Type;
import compiler.semantics.TypeVisitor;

public class UninitVariable extends VariableGod {
    TypeDeclaration type;
    Expression identifier;
    public UninitVariable(int line, TypeDeclaration type,Expression identifier) {
        super(line);
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public String getVariableName() {
        return identifier().getRep();
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.println(indentation+"UninitVariable: ");
        System.out.printf(indentation+"  - type: %s\n", type.toString());
        System.out.printf(indentation+"  - identifier: %s\n", identifier.toString());
    }

    @Override
    public String toString() {
        return "UninitVar{ " +
                 type +
                 ", " + identifier +
                " }";
    }

    @Override
    public Type getType() {
        return new Type(type.type.image(),type.isArray);
    }

    @Override
    public boolean equals(Object o) {
        UninitVariable u = (UninitVariable) o;
        
        if(!this.type.equals(u.type)) return false;
        else if(!this.identifier.equals(u.identifier)) return false;
        
        return true;
    }
    @Override
    public TypeDeclaration typeDeclaration() {
        return type;
    }

    @Override
    public Expression identifier() {
        return this.identifier;
    }

    @Override
    public Expression declarator() {
        return null;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitVariable(this);

    }
}
