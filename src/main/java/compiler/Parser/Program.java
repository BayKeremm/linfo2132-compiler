package compiler.Parser;
import java.util.ArrayList;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;
/**
* This is the root of the AST produced by the parser
* */
public class Program extends ASTNode{
    private String fileName;
    public ArrayList<ConstantVariable> constantVariables;
    public ArrayList<Procedure> procedures;
    public ArrayList<StructDeclaration> structDeclarations;
    public ArrayList<Statement> globals;

    public Program(String fileName, ArrayList<ConstantVariable> constantVariables, ArrayList<Statement> globals, ArrayList<StructDeclaration> structDeclarations, ArrayList<Procedure> procedures) {
        this.fileName = fileName;
        this.constantVariables = constantVariables;
        this.procedures = procedures;
        this.structDeclarations = structDeclarations;
        this.globals = globals;
        program = this;

    }
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

    @Override
    public boolean equals(Object o){

        Program p = (Program) o;

        if(!this.constantVariables.equals(p.constantVariables)) return false;
        else if(!this.procedures.equals(p.procedures)) return false;
        else if(!this.structDeclarations.equals(p.structDeclarations)) return false;
        else if(!this.globals.equals(p.globals)) return false;
        
        return true;
    }

    public void addConstantVariable(ConstantVariable c){
        constantVariables.add(c);
    }

    public void addStructDeclaration(StructDeclaration s){
        structDeclarations.add(s);
    }

    public void addProcedure(Procedure p){
        procedures.add(p);
    }

    public void addGlobal(Statement s){
        globals.add(s);
    }
}
