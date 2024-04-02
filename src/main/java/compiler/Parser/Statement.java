package compiler.Parser;

import compiler.Lexer.Symbol;

public abstract class Statement extends ASTNode implements StatementChecker {
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

}
abstract class  VariableGod extends Statement  {

    protected VariableGod(int line) {
        super(line);
    }
    public abstract TypeDeclaration typeDeclaration();
    public abstract Expression identifier();
    public abstract Expression declarator();
}
class Variable extends VariableGod {
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
    public void typeAnalyse(NodeVisitor v) {
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
}
class ConstantVariable extends VariableGod {
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
    public void typeAnalyse(NodeVisitor v) {
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
class ScopeVariable extends VariableGod {
    Expression identifier;
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
    public void typeAnalyse(NodeVisitor v) {
        v.visitScopeVariable(this);

    }
}
class UninitVariable extends VariableGod {
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
    public void typeAnalyse(NodeVisitor v) {
        v.visitVariable(this);

    }
}

class StructDeclaration extends Statement  {
    Expression identifier;
    Block block;

    protected StructDeclaration(int line, Expression identifier, Block block) {
        super(line);
        this.identifier = identifier;
        this.block = block;
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

    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitStructDeclaration(this);
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
    public String toString() {
        return type.image() + (isArray ? "[]":"") ;
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

    @Override
    public void typeAnalyse(NodeVisitor v) {

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
    public void typeAnalyse(NodeVisitor v) {
        v.visitArrayInitializer(this);
    }

    @Override
    public GenericType getType() {
        return new Type(type.type.token().image(),type.isArray);
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

        return true;
    }
}

