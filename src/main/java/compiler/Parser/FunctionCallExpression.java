package compiler.Parser;

import compiler.Lexer.Symbol;
import compiler.semantics.TypeVisitor;

import java.util.ArrayList;

public class FunctionCallExpression extends PrimaryExpression{
    Symbol identifier;
    ArrayList<Expression> expressionParams;

    public Symbol getFunctionIdentifier() {
        return identifier;
    }

    public ArrayList<Expression> getFunctionExpressionParams() {
        return expressionParams;
    }

    GenericType type;

    public FunctionCallExpression(int line, Symbol identifier,ArrayList<Expression> expressionParams ) {
        super(line);
        this.identifier= identifier;
        this.expressionParams = expressionParams;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        this.type = v.visitSymbolTableFunction(this);
        v.visitFunctionCallExpression(this);
    }

    @Override
    public GenericType getType() {
        return this.type;
    }

    @Override
    public String getRep() {
        System.out.printf("%s(", identifier.image());
        String string = "";
        for(Expression e : expressionParams){
            string = string.concat(e.getRep());
        }
        return string + ")";
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Function call:\n");
        System.out.printf(indentation+"  - identifier:%s\n",identifier);
        int i = 1;
        for(Expression e : expressionParams){
            System.out.printf(indentation+"  parameter %d:\n",i);
            e.prettyPrint(indentation+"     ");
            i++;
        }
        System.out.println();

    }

    @Override
    public String toString() {
        return "FunctionCallExpression{" +
                "identifier=" + identifier +
                ", expressionParams=" + expressionParams +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        FunctionCallExpression a = (FunctionCallExpression) o;
        
        if(!this.identifier.equals(a.identifier)) return false;
        else if(!this.expressionParams.equals(a.expressionParams)) return false;

        return true;
    }
}
