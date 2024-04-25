package compiler.Parser;

public class LogicalOr extends LogicalExpression{
    public LogicalOr(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "||");
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"OR op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        LogicalOr a = (LogicalOr) o;

        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
