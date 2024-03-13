package compiler.Parser;

import java.util.ArrayList;

public class FunctionCallExpression extends PrimaryExpression{
    String functionName;
    ArrayList<Expression> expressionParams;

    public FunctionCallExpression(int line, String functionName,ArrayList<Expression> expressionParams ) {
        super(line);
        this.functionName = functionName;
        this.expressionParams = expressionParams;
    }
}
