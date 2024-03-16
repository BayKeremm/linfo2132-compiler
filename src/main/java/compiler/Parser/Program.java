package compiler.Parser;

import java.util.ArrayList;

/**
* This is the root of the AST produced by the parser
* */
public class Program extends ASTNode{
    private String fileName;
    private ArrayList<ConstantVariable> constantVariables;
    private ArrayList<Procedure> procedures;
    private ArrayList<StructDeclaration> structDeclarations;
    private ArrayList<Statement> globals;

    protected Program(String fileName, ArrayList<ConstantVariable> constantVariables, ArrayList<Statement> globals, ArrayList<StructDeclaration> structDeclarations, ArrayList<Procedure> procedures) {
        this.fileName = fileName;
        this.constantVariables = constantVariables;
        this.procedures = procedures;
        this.structDeclarations = structDeclarations;
        this.globals = globals;
        program = this;
    }
    @Override
    public void printNode() {
        for(ConstantVariable c : constantVariables){
            c.prettyPrint(" ");
        }
        for(StructDeclaration s : structDeclarations){
            s.prettyPrint(" ");
        }
        for(Statement s : globals){
            s.prettyPrint(" ");
        }
        for(Procedure p : procedures){
            p.prettyPrint(" ");
        }

    }

}
