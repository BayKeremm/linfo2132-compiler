package compiler.Parser;

import compiler.Lexer.Symbol;

abstract class Statement extends ASTNode {
    int line;
    protected Statement(int line) {
        this.line = line;
    }

}
class VarDeclarator extends Statement{
    Symbol type;
    Expression expression;
    Symbol identifier;
    protected VarDeclarator(int line, Symbol type, Expression expression, Symbol identifier) {
        super(line);
        this.type = type;
        this.expression = expression;
        this.identifier = identifier;
    }

    @Override
    public void printNode() {
        System.out.printf("    - type: %s \n", type.image());
        System.out.printf("    - identifier: %s \n", identifier.image());
        System.out.print("    - Expression: ");
        expression.printNode();
    }
}
class LocalVariable extends Statement {
    VarDeclarator declarator;
    public LocalVariable(int line, VarDeclarator declarator) {
        super(line);
        this.declarator = declarator;
    }

    @Override
    public void printNode() {
        System.out.println("\nLocal variable:");
        declarator.printNode();

    }
}
class ConstantVariable extends Statement {
    VarDeclarator declarator;
    public ConstantVariable(int line, VarDeclarator declarator) {
        super(line);
        this.declarator = declarator;
    }

    @Override
    public void printNode() {
        System.out.println("\nConstant variable:");
        declarator.printNode();

    }
}

