package compiler.Parser;

import compiler.Lexer.Symbol;

import java.util.ArrayList;

/**
 * PRIMARY EXPRESSION:
 *          parExpression -> LPARAN expression (COMMA expression)* RPARAN
 *          primary -> qualifiedIdentifier (parExpression) | literal | parExpression
 * */
abstract class PrimaryExpression extends Expression{
    public PrimaryExpression(int line) {
        super( line);
    }
}
class FunctionCallExpression extends PrimaryExpression{
    Symbol identifier;
    ArrayList<Expression> expressionParams;

    public FunctionCallExpression(int line, Symbol identifier,ArrayList<Expression> expressionParams ) {
        super(line);
        this.identifier= identifier;
        this.expressionParams = expressionParams;
    }
}

class LiteralExpression extends PrimaryExpression{
    Symbol literal;
    public LiteralExpression(int line, Symbol literal) {
        super(line);
        this.literal = literal;
    }
}
class ParanExpression extends PrimaryExpression{
    ArrayList<Expression> expressions;
    public ParanExpression(int line, ArrayList<Expression> expressions) {
        super(line);
        this.expressions = expressions;
    }
}
