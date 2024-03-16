package compiler.Parser;

import compiler.Lexer.Symbol;

abstract class Statement extends ASTNode {
    int line;
    protected Statement(int line) {
        this.line = line;
    }

}
class LocalVariable extends Statement {
    Symbol type;
    Expression identifier;
    Expression declarator;
    public LocalVariable(int line, Symbol type, Expression identifier,  Expression declarator) {
        super(line);
        this.declarator = declarator;
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\nLocal variable:");
        System.out.printf("    - type: %s \n", type.image());
        System.out.print("    - identifier:");
        identifier.printNode();
        declarator.printNode();

    }
}
class ConstantVariable extends Statement {
    Symbol type;
    Expression identifier;
    Expression declarator;
    public ConstantVariable(int line, Symbol type, Expression identifier, Expression declarator) {
        super(line);
        this.declarator = declarator;
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\nConstant variable:");
        System.out.printf("    - type: %s \n", type.image());
        System.out.print("    - identifier:");
        identifier.printNode();
        declarator.printNode();

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
}
class UninitVariable extends Statement {
    Symbol type;
    Expression identifier;
    public UninitVariable(int line, Symbol type,Expression identifier) {
        super(line);
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\n Uninit variable:");
        System.out.printf("    - type: %s \n", type.image());
        System.out.print("    - identifier:");
        identifier.printNode();
    }
}

