package compiler.Parser;

import compiler.semantics.TypeVisitor;

/**
 * PRIMARY EXPRESSION:
 *          parExpression -> LPARAN expression (COMMA expression)* RPARAN
 *          primary -> qualifiedIdentifier (parExpression) | literal | parExpression
 * */
public abstract class PrimaryExpression extends Expression{
    public PrimaryExpression(int line) {
        super( line);
    }
    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitPrimaryExpression(this);

    }
}
