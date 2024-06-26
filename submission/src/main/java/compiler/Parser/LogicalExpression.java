package compiler.Parser;

import compiler.Lexer.Token;

/** LOGICAL EXPRESSION:
 *             logicalExpression -> equalityExpression ((LAND | LOR ) logicalExpression)*
 * */
public abstract  class LogicalExpression extends Expression {
    public LogicalExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public GenericType getType() {
        GenericType before_type = super.getType();
        if(before_type.type().contains("With")){
            return before_type;
        }else{
            return new Type(Token.BOOLEAN.image(), false);
        }
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitLogicalExpression(this);
    }
}

