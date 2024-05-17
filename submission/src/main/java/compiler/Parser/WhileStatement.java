package compiler.Parser;

import compiler.bytecodegen.ByteVisitor;

public class WhileStatement extends Statement {
    Expression condition;

    public Expression getWhileCondition() {
        return condition;
    }

    public Block getWhileBlock() {
        return block;
    }

    Block block;
    public WhileStatement(int line, Expression condition, Block block) {
        super(line);
        this.condition = condition;
        this.block = block;
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.println(indentation+"While Statement");
        System.out.println(indentation+"  - While condition:");
        condition.prettyPrint(indentation+"    ");
        System.out.println(indentation+"  - While block:");
        block.prettyPrint(indentation+"     ");

    }

    @Override
    public String toString() {
        return "WhileStatement{ " +
                "( " + condition + " )"+
                "," + block +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        WhileStatement w = (WhileStatement) o;

        if(!this.condition.equals(w.condition)) return false;
        else if(!this.block.equals(w.block)) return false;
        
        return true;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitWhile(this);

    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitWhile(this);

    }
}
