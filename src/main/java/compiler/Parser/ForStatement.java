package compiler.Parser;

import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class ForStatement extends Statement{
    public Block getForBlock() {
        return block;
    }

    public Statement getPos0() {
        return pos0;
    }

    public Statement getPos2() {
        return pos2;
    }

    public Expression getPos1() {
        return pos1;
    }

    Block block;
    Statement pos0,pos2;
    Expression pos1;
    protected ForStatement(int line, Statement pos0,  Expression pos1, Statement pos2, Block block) {
        super(line);
        this.block = block;
        this.pos0 = pos0;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"For statement: \n");
        System.out.print(indentation+"  - Expressions:\n");
        System.out.print(indentation+"     - Pos1:\n");
        pos0.prettyPrint(indentation+"      ");
        System.out.print(indentation+"     - Pos2:\n");
        pos1.prettyPrint(indentation+"      ");
        System.out.print(indentation+"     - Pos3:\n");
        pos2.prettyPrint(indentation+"      ");
        System.out.println(indentation+"  - For block:");
        block.prettyPrint(indentation+"     ");

    }

    @Override
    public String toString() {
        return "ForStatement{" +
                 block +
                pos0 +
                 pos2 +
                  pos1 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        ForStatement f = (ForStatement) o;

        if(!this.block.equals(f.block)) return false;
        else if(!this.pos0.equals(f.pos0)) return false;
        else if(!this.pos1.equals(f.pos1)) return false;
        else if(!this.pos2.equals(f.pos2)) return false;
        
        return true;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitFor(this);

    }

    @Override
    public void codeGen(ByteVisitor b) {

    }
}
