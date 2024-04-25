package compiler.Parser;

import compiler.semantics.TypeVisitor;

public class IfElseStatement extends Statement {
    Expression ifCondition;

    public Expression getIfCondition() {
        return ifCondition;
    }

    public Block getIfBlock() {
        return ifBlock;
    }

    public Block getElseBlock() {
        return elseBlock;
    }

    Block ifBlock;
    Block elseBlock;

    protected IfElseStatement(int line, Expression ifCondition,Block ifBlock, Block elseBlock) {
        super(line);
        this.ifCondition=ifCondition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"If: %s\n", ifCondition);
        ifBlock.prettyPrint(indentation+"  ");
        if(elseBlock != null){
            System.out.print(indentation+"Else:\n");
            elseBlock.prettyPrint(indentation+"  ");
        }

    }

    @Override
    public boolean equals(Object o) {
        IfElseStatement i = (IfElseStatement) o;

        if (!this.ifCondition.equals(i.ifCondition)) return false;
        else if (!this.ifBlock.equals(i.ifBlock)) return false;
        else if (!this.elseBlock.equals(i.elseBlock)) return false;

        return true;
    }
    

    public String toString() {
        return "IfElseStatement{" +
                "ifCondition=" + ifCondition +
                ",ifBlock=" + ifBlock +
                ",elseBlock=" + elseBlock +
                "}";
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitIfElse(this);

    }
}
