package compiler.Parser;


import compiler.Lexer.Symbol;

import java.util.ArrayList;

class Procedure extends Statement {
    ProcedureDeclarator declarator;
    Symbol returnType;
    Symbol identifier;

    protected Procedure(int line,ProcedureDeclarator declarator, Symbol returnType, Symbol identifier) {
        super(line);
        this.returnType = returnType;
        this.identifier = identifier;
        this.declarator = declarator;
    }

    @Override
    public void printNode() {
        System.out.println("\n Procedure:");
        System.out.printf("     - procedure name: %s\n", identifier.image());
        System.out.printf("     - return type: %s\n", returnType.image());
        declarator.printNode();
    }
}
class ProcedureDeclarator extends Statement {
    ArrayList<Parameter> parameters;
    Block block;

    protected ProcedureDeclarator(int line, ArrayList<Parameter> parameters, Block block) {
        super(line);
        this.parameters = parameters;
        this.block = block;
    }

    @Override
    public void printNode() {
        System.out.print("     - Parameters: ");
        for(Parameter p: parameters){
            System.out.printf(" %s ",p.getRep());
        }
        System.out.print("\n     - Block: ");
        block.printNode();

    }
}
class Parameter extends Expression {
    Symbol type;
    Expression expression;
    protected Parameter(int line, Symbol type, Expression expression) {
        super(line);
        this.type = type;
        this.expression = expression;
    }

    @Override
    public String getRep() {
        return type.image() + " " +  expression.getRep();
    }

    @Override
    public void printNode() {

    }


}
class Block extends Statement{
    ArrayList<Statement> statements;

    protected Block(int line,ArrayList<Statement> statements ) {
        super(line);
        this.statements = statements;
    }
    @Override
    public void printNode() {
        for(Statement s : statements){
            s.printNode();
        }

    }
}

class IfElseStatement extends Statement {

    protected IfElseStatement(int line) {
        super(line);
    }

    @Override
    public void printNode() {

    }
}

