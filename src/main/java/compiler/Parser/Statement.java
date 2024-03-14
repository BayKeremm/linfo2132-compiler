package compiler.Parser;

import compiler.Lexer.Symbol;

abstract class Statement extends ASTNode {
    protected Statement(int line) {
        super(line);
    }
}
class Variable extends Statement{
    Symbol identifier;
    VarDeclarator declarator;
    public Variable(int line, Symbol identifier, VarDeclarator declarator ) {
        super(line);
        this.identifier = identifier;
        this.declarator = declarator;
    }

    @Override
    public void printNode() {

    }
}
class ConstantVariable extends Statement{
    Symbol type;
    Symbol identifier;
    Expression expression;
    public ConstantVariable(int line, Symbol type, Symbol identifier, Expression expression) {
        super(line);
        this.type = type;
        this.identifier = identifier;
        this.expression = expression;
    }

    @Override
    public void printNode() {
        System.out.println("\nConstant variable:");
        System.out.printf("    - type: %s \n", type.image());
        System.out.printf("    - identifier: %s \n", identifier.image());
        System.out.print("    - Expression: ");

        expression.printExpression();

    }
}