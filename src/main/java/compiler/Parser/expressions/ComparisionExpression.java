package compiler.Parser.expressions;

import compiler.Lexer.Token;
import compiler.semantics.GenericType;
import compiler.semantics.Type;
import compiler.semantics.TypeVisitor;

/** COMPARISON EXPRESSION:
 *              comparisonExpression -> termExpression ( ( LT | GT | LE | GE ) termExpression )*
 * */
public abstract class ComparisionExpression extends Expression {
    public ComparisionExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }


    @Override
    public GenericType getType() {
        GenericType before_type = super.getType();
        if(before_type.type().equals("ERROR")){
            return before_type;
        }else{
            return new Type(Token.BOOLEAN.image(),false);
        }
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitComparisonExpression(this);
    }
}
