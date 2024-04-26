package compiler.Parser.expressions;

import compiler.Lexer.Token;
import compiler.Parser.GenericType;
import compiler.semantics.Type;
import compiler.semantics.TypeVisitor;

/** EQUALITY EXPRESSION:
 *             equalityExpression -> comparisonExpression ( ( NOT_EQUAL | EQUAL ) comparisonExpression  )*
 * */
public abstract class EqualityCheckExpression extends Expression {

    public EqualityCheckExpression(int line, Expression lhs, Expression rhs, String operator) {
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
        v.visitEqualityCheckExpression(this);
    }
}
