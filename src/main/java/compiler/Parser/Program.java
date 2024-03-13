package compiler.Parser;

import java.util.ArrayList;

/**
* This is the root of the AST produced by the parser
* */
public class Program extends ASTNode{
    private String fileName;
    private ArrayList<ConstantVariable> constantVariables;

    protected Program(int line, ArrayList<ConstantVariable> constantVariables, String fileName) {
        super(line);
        this.fileName = fileName;
        this.constantVariables = constantVariables;
        program = this;
    }

    public void printProgram(){
        for(ConstantVariable c : constantVariables){
            c.printNode();
        }
    }
}
