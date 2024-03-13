package compiler.Parser;

import compiler.Lexer.Symbol;

public class ConstantVariable extends ASTNode{
    Symbol type;
    Symbol identifier;
    Expression expression;
    protected ConstantVariable(int line, Symbol type, Symbol identifier, Expression expression) {
        super(line);
        this.type = type;
        this.identifier = identifier;
        this.expression = expression;
    }

    @Override
    public void printNode() {
        super.printNode();
        System.out.println("Constant variable:");
        System.out.printf("    - type: %s ", type.image());
        System.out.printf("    - identifier: %s ", identifier.image());
        System.out.print("    - Expression: ");
        expression.printExpression();

    }
}
