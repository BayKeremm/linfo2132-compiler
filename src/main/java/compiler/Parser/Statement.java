package compiler.Parser;

import compiler.Lexer.Symbol;

import java.util.ArrayList;

public abstract class Statement extends ASTNode {
    int line;
    protected Statement(int line) {
        this.line = line;
    }
    public abstract void prettyPrint(String indentation);

}
class Variable extends Statement {
    TypeDeclaration type;
    Expression identifier;
    Expression declarator;
    public Variable(int line, TypeDeclaration type, Expression identifier, Expression declarator) {
        super(line);
        this.declarator = declarator;
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\nVariable:");
        System.out.printf("    - type: %s \n", type.toString());
        System.out.print("    - identifier:");
        identifier.printNode();
        declarator.printNode();

    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.println(indentation+"Variable: ");
        System.out.printf(indentation+"  - type: %s\n", type.toString());
        System.out.printf(indentation+"  - identifier: %s\n", identifier.toString());
        //System.out.printf(indentation+"- declaration: %s\n", declarator.toString());
        System.out.println(indentation+"  - declaration:");
        declarator.prettyPrint(indentation+"    ");


    }

    @Override
    public String toString() {
        return "Variable{" +
                 type +
                ", " + identifier +
                ", (" + declarator+
                ") }";
    }

    @Override
    public boolean equals(Object o) {
        Variable v = (Variable) o;

        if(!this.type.equals(v.type)) return false;
        else if(!this.declarator.equals(v.declarator)) return false;
        else if(!this.identifier.equals(v.identifier)) return false;
        
        return true;
    }
}
class ConstantVariable extends Statement {
    TypeDeclaration type;
    Expression identifier;
    Expression declarator;
    public ConstantVariable(int line, TypeDeclaration type, Expression identifier, Expression declarator) {
        super(line);
        this.declarator = declarator;
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\nConstant variable:");
        System.out.print("    - type:");
        type.printNode();
        System.out.print("    - identifier:");
        identifier.printNode();
        declarator.printNode();

    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print("Constant variable: \n");
        System.out.printf(indentation+"- type: %s\n", type.toString());
        System.out.printf(indentation+"- identifier: %s\n", identifier.toString());
        System.out.print(indentation+"- declaration:\n");
        declarator.prettyPrint(indentation+"  ");
        //System.out.printf(indentation+"- declarator: %s\n", declarator.toString());
    }

    @Override
    public boolean equals(Object o) {
        ConstantVariable c = (ConstantVariable) o;

        if(!this.type.equals(c.type)) return false;
        else if(!this.identifier.equals(c.identifier)) return false;
        else if(!this.declarator.equals(c.declarator)) return false;
        
        return true;
    }
}
class ScopeVariable extends Statement {
    Expression identifier;
    Expression declarator;
    public ScopeVariable(int line, Expression identifier, Expression declarator) {
        super(line);
        this.declarator = declarator;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\nScope variable:");
        System.out.print("    - identifier:");
        identifier.printNode();
        declarator.printNode();

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
}
class UninitVariable extends Statement {
    TypeDeclaration type;
    Expression identifier;
    public UninitVariable(int line, TypeDeclaration type,Expression identifier) {
        super(line);
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println(" \nUninit variable:");
        System.out.printf("    - type: %s \n", type.toString());
        System.out.print("    - identifier:");
        identifier.printNode();
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
    public boolean equals(Object o) {
        UninitVariable u = (UninitVariable) o;
        
        if(!this.type.equals(u.type)) return false;
        else if(!this.identifier.equals(u.identifier)) return false;
        
        return true;
    }
}

class StructDeclaration extends Statement {
    Expression identifier;
    Block block;

    protected StructDeclaration(int line, Expression identifier, Block block) {
        super(line);
        this.identifier = identifier;
        this.block = block;
    }

    @Override
    public void printNode() {
        System.out.println("\nStruct Declaration:");
        identifier.printNode();
        block.printNode();
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.println("Struct Declaration: ");
        System.out.printf(indentation+"- identifier: %s\n",identifier.toString());
        //System.out.printf(indentation+"- block: %s\n",block.toString());

        block.prettyPrint(indentation+" ");


    }

    @Override
    public boolean equals(Object o) {
        StructDeclaration s = (StructDeclaration) o;

        if(!this.block.equals(s.block)) return false;
        else if(!this.identifier.equals(s.identifier)) return false;
        
        return true;
    }
}

class TypeDeclaration extends Statement {
    Symbol type;
    Boolean isArray;

    protected TypeDeclaration(int line, Symbol type, Boolean isArray) {
        super(line);
        this.type = type;
        this.isArray = isArray;
    }

    @Override
    public void printNode() {
        System.out.printf("%s%s",type.image(),(isArray ? "[]":""));

    }

    @Override
    public String toString() {
        return "TypeDec{"+type.image() + (isArray ? "[]":"") + "}";
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"Type dec: %s",type.image());
        if(isArray){
            System.out.print("[]\n");
        }

    }

    @Override
    public boolean equals(Object o) {
        TypeDeclaration t = (TypeDeclaration) o;

        if(!this.type.equals(t.type)) return false;
        else if(!(this.isArray == t.isArray)) return false;

        return true;
    }
}

class ArrayInitializer extends Expression {
    TypeDeclaration type;
    Expression size;
    protected ArrayInitializer(int line, TypeDeclaration type, Expression size) {
        super(line);
        this.type = type;
        this.size = size;
    }

    @Override
    public String getRep() {
        return null;
    }

    @Override
    public void printNode() {

    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"- Array init:\n");

        System.out.print(indentation+"   - type:\n");
        type.prettyPrint(indentation+"      ");
        System.out.print(indentation+"   - size:\n");
        size.prettyPrint(indentation+"      ");

    }

    @Override
    public String toString() {
        return "ArrayInitializer{" +
                 type +
                ", " + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        ArrayInitializer a = (ArrayInitializer) o;

        if(!this.size.equals(a.size)) return false;
        else if(!this.type.equals(a.type)) return false;
        else if (!this.operator.equals(a.operator)) return false;
        else if (!this.lhs.equals(a.lhs)) return false;
        else if (!this.rhs.equals(a.rhs)) return false;    
    
        return true;
    }
}

/*
class StructInitializer extends Expression{
    Expression identifier;
    ArrayList<Expression> structParams;

    public StructInitializer(int line, Expression identifier, ArrayList<Expression> structParams) {
        super(line);
        this.identifier = identifier;
        this.structParams = structParams;
    }

    @Override
    public void printNode() {

    }

    @Override
    public String getRep() {
        return null;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print("Unimplemented pretty print struct init");
    }
}
 */
