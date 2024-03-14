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
    Symbol identifier;
    protected VarDeclarator(int line, Expression expression, Symbol identifier) {
        super(line);
        this.expression = expression;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.printf("    - identifier: %s \n", identifier.image());
        System.out.print("    - Expression: ");
        expression.printNode();
    }
}
class LocalVariable extends Statement {
    Symbol type;
    VarDeclarator declarator;
    public LocalVariable(int line, Symbol type,  VarDeclarator declarator) {
        super(line);
        this.declarator = declarator;
        this.type = type;
    }

    @Override
    public void printNode() {
        System.out.println("\nLocal variable:");
        System.out.printf("    - type: %s \n", type.image());
        declarator.printNode();

    }
}
class ConstantVariable extends Statement {
    Symbol type;
    VarDeclarator declarator;
    public ConstantVariable(int line, Symbol type, VarDeclarator declarator) {
        super(line);
        this.declarator = declarator;
        this.type = type;
    }

    @Override
    public void printNode() {
        System.out.println("\nConstant variable:");
        System.out.printf("    - type: %s \n", type.image());
        declarator.printNode();

    }
}
class ScopeVariable extends Statement {
    VarDeclarator declarator;
    public ScopeVariable(int line, VarDeclarator declarator) {
        super(line);
        this.declarator = declarator;
    }

    @Override
    public void printNode() {
        System.out.println("\nScope variable:");
        declarator.printNode();

    }
}

