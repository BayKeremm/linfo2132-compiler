package compiler.Parser;
import java.util.ArrayList;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

/**
* This is the root of the AST produced by the parser
* */
public class Program extends ASTNode{
    private String fileName;
    private ArrayList<ConstantVariable> constantVariables;
    private ArrayList<Procedure> procedures;
    private ArrayList<StructDeclaration> structDeclarations;
    private ArrayList<Statement> globals;

    public Program(String fileName, ArrayList<ConstantVariable> constantVariables, ArrayList<Statement> globals, ArrayList<StructDeclaration> structDeclarations, ArrayList<Procedure> procedures) {
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
}
