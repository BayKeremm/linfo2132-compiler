package compiler.Parser.expressions;

import compiler.Lexer.Token;
import compiler.Parser.GenericType;
import compiler.Parser.statements.Statement;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.Type;

public abstract class Expression extends Statement {
    public Expression getLhs() {
        return lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    public String getOperator() {
        return operator;
    }

    Expression lhs;
    Expression rhs;
    String operator;
    public Expression(int line, Expression lhs, Expression rhs, String operator) {
        super(line);
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }

    public Expression(int line) {
        super(line);
    }

    public abstract String getRep();

    public void prepCodeGen(ByteVisitor b){

    }

    public GenericType getType(){
        GenericType tl = this.lhs.getType();
        GenericType tr = this.rhs.getType();
        if(tl.type().equals(tr.type())){
            return tl;
        } else if (tl.type().equals(Token.FLOAT.image()) && tr.type().equals(Token.INTEGER.image())) {
            return tl;
        } else if (tr.type().equals(Token.FLOAT.image()) && tl.type().equals(Token.INTEGER.image())) {
            return tr;
        }
        return new Type("With attempted type of:" + lhs.getType() + " "+ operator+" "+  rhs.getType(), false);
    }
    @Override
    public String toString() {
        return  lhs + " '" + operator +"' "+ rhs;
    }

    @Override
    public void prettyPrint(String indentation) {
        if(lhs != null){
            System.out.printf(indentation+"  LHS:\n");
            lhs.prettyPrint(indentation+"   ");
        }
        if(rhs != null){
            System.out.printf(indentation+"  RHS:\n");
            rhs.prettyPrint(indentation+"   ");
        }


    }
}
