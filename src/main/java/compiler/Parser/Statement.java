package compiler.Parser;

import compiler.Lexer.Symbol;

abstract class Statement extends ASTNode {
    int line;
    protected Statement(int line) {
        this.line = line;
    }

}
class VarDeclarator extends Statement{
    Expression expression;
    protected VarDeclarator(int line, Expression expression) {
        super(line);
        this.expression = expression;
    }

    @Override
    public void printNode() {
        System.out.print("    - Expression: ");
        expression.printNode();
    }
}
class LocalVariable extends Statement {
    Symbol type;
    Symbol identifier;
    VarDeclarator declarator;
    public LocalVariable(int line, Symbol type, Symbol identifier,  VarDeclarator declarator) {
        super(line);
        this.declarator = declarator;
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\nLocal variable:");
        System.out.printf("    - type: %s \n", type.image());
        System.out.printf("    - identifier: %s \n", identifier.image());
        declarator.printNode();

    }
}
class ConstantVariable extends Statement {
    Symbol type;
    Symbol identifier;
    VarDeclarator declarator;
    public ConstantVariable(int line, Symbol type, Symbol identifier, VarDeclarator declarator) {
        super(line);
        this.declarator = declarator;
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\nConstant variable:");
        System.out.printf("    - type: %s \n", type.image());
        System.out.printf("    - identifier: %s \n", identifier.image());
        declarator.printNode();

    }
}
class ScopeVariable extends Statement {
    VarDeclarator declarator;
    Symbol identifier;
    public ScopeVariable(int line, Symbol identifier, VarDeclarator declarator) {
        super(line);
        this.declarator = declarator;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\nScope variable:");
        System.out.printf("    - identifier: %s \n", identifier.image());
        declarator.printNode();

    }
}
class UninitVariable extends Statement {
    Symbol type;
    Symbol identifier;
    public UninitVariable(int line, Symbol type,Symbol identifier) {
        super(line);
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.println("\n Uninit variable:");
        System.out.printf("    - type: %s \n", type.image());
        System.out.printf("    - identifier: %s \n", identifier.image());
    }
}

