package compiler.Parser.expressions;

import compiler.semantics.GenericType;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

import java.util.ArrayList;

public class ParanExpression extends PrimaryExpression {
    public ArrayList<Expression> getParanExpressions() {
        return expressions;
    }

    // expression size is always 1
    ArrayList<Expression> expressions;
    public ParanExpression(int line, ArrayList<Expression> expressions) {
        super(line);
        this.expressions = expressions;
    }

    @Override
    public GenericType getType() {
        assert expressions.size() == 1;
        return expressions.get(0).getType();
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitParanExpression(this);
    }

    @Override
    public String getRep() {
        String string = "(";
        for(Expression e : expressions){
            string = string.concat(e.getRep());
        }
        return string + ")";
    }

    @Override
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);
        for(Expression e : expressions){
            e.prettyPrint(indentation);
        }


    }

    @Override
    public String toString() {
        return "ParanExps{ " +
                 expressions +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        return this.expressions.equals(((ParanExpression) o).expressions);
    }

    @Override
    public void codeGen(ByteVisitor b) {
        expressions.get(0).codeGen(b);

    }
}
