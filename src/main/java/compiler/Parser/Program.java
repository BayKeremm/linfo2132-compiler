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
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_PURPLE = "\u001B[35m";
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_WHITE = "\u001B[37m";
        final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
        final String ANSI_RED_BACKGROUND = "\u001B[41m";
        final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
        final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
        final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
        final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
        final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
        final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
        System.out.print(ANSI_CYAN);
        for(ConstantVariable c : constantVariables){
            c.prettyPrint(" ");
        }
        System.out.print(ANSI_RESET);
        System.out.print(ANSI_YELLOW);
        for(StructDeclaration s : structDeclarations){
            s.prettyPrint(" ");
        }
        System.out.print(ANSI_RESET);
        System.out.print(ANSI_GREEN);
        for(Statement s : globals){
            s.prettyPrint(" ");
        }
        System.out.print(ANSI_RESET);
        int i = 0;
        for(Procedure p : procedures){
            System.out.print(ANSI_RESET);
            p.prettyPrint(" ");
            i++;
        }

    }

}
