package compiler.Parser;

import compiler.semantics.TypeVisitor;

import java.util.ArrayList;

public class ProcedureDeclarator extends Statement {
    ArrayList<Expression> parameters;
    Block block;

    public ArrayList<Expression> getProDecParameters() {
        return parameters;
    }

    public Block getProDecBlock() {
        return block;
    }

    protected ProcedureDeclarator(int line, ArrayList<Expression> parameters, Block block) {
        super(line);
        this.parameters = parameters;
        this.block = block;
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"- Parameters:\n");
        int i = 1;
        for(Expression p: parameters){
            System.out.printf(indentation+"  Param %d:\n",i);
            p.prettyPrint(indentation+"  ");
            i++;
        }
        System.out.printf(indentation+"- Block:\n");
        block.prettyPrint(indentation+"    ");

    }

    @Override
    public boolean equals(Object o) {
        ProcedureDeclarator p = (ProcedureDeclarator) o;

        if(!this.parameters.equals(p.parameters)) return false;
        else if(!this.block.equals(p.block)) return false;
        
        return true;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {

    }
}
