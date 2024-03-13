package compiler.Parser;

import compiler.Lexer.Symbol;

public class LiteralExpression extends PrimaryExpression{
    Symbol literal;
    public LiteralExpression(int line, Symbol literal) {
        super(line);
        this.literal = literal;
    }

    @Override
    public void printExpression() {
        super.printExpression();
        System.out.println("Hello from Literal expression");
    }
}
