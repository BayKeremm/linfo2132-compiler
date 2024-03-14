package compiler.Parser;

import java.util.ArrayList;

/**
* This is the root of the AST produced by the parser
* */
public class Program extends ASTNode{
    private String fileName;
    private ArrayList<ConstantVariable> constantVariables;
    private ArrayList<Procedure> procedures;

    protected Program(String fileName, ArrayList<ConstantVariable> constantVariables,ArrayList<Procedure> procedures) {
        this.fileName = fileName;
        this.constantVariables = constantVariables;
        this.procedures = procedures;
        program = this;
    }
    @Override
    public void printNode() {
        for(ConstantVariable c : constantVariables){
            c.printNode();
        }
        for(Procedure p : procedures){
            p.printNode();
        }

    }

}
