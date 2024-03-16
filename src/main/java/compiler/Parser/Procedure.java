package compiler.Parser;


import com.google.errorprone.annotations.Var;
import compiler.Lexer.Symbol;

import javax.swing.plaf.nimbus.State;
import java.lang.reflect.Array;
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
    Expression ifCondition;
    Block ifBlock;
    Block elseBlock;

    protected IfElseStatement(int line, Expression ifCondition,Block ifBlock, Block elseBlock) {
        super(line);
        this.ifCondition=ifCondition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void printNode() {
        System.out.printf("\nIf %s:\n", ifCondition.getRep());
        ifBlock.printNode();
        System.out.print("\nElse:\n");
        elseBlock.printNode();
    }
}

class WhileStatement extends Statement {
    Expression condition;
    Block block;
    protected WhileStatement(int line, Expression condition, Block block) {
        super(line);
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void printNode() {
        System.out.printf("\nwhile %s:\n", condition.getRep());
        block.printNode();

    }
}

class ForStatement extends Statement{
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
    public void printNode() {
        System.out.print("For statement: ");
        pos0.printNode();
        pos1.printNode();
        pos2.printNode();
        block.printNode();
    }
}

class ReturnStatement extends Statement {
    Expression expression;

    protected ReturnStatement(int line, Expression expression) {
        super(line);
        this.expression = expression;
    }

    @Override
    public void printNode() {
        System.out.print("\n\tReturn: ");
        expression.printNode();


    }
}

