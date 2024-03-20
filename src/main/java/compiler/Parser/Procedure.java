package compiler.Parser;


import compiler.Lexer.Symbol;

import java.util.ArrayList;

public class Procedure extends Statement {
    ProcedureDeclarator declarator;
    TypeDeclaration returnType;
    Symbol identifier;

    public Procedure(int line,ProcedureDeclarator declarator, TypeDeclaration returnType, Symbol identifier) {
        super(line);
        this.returnType = returnType;
        this.identifier = identifier;
        this.declarator = declarator;
    }

    @Override
    public void printNode() {
        System.out.println("\n Procedure:");
        System.out.printf("     - procedure name: %s\n", identifier.image());
        System.out.print("     - return type: ");
        returnType.printNode();
        declarator.printNode();
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.println("Procedure: ");
        System.out.printf(indentation+"- return type: %s\n", returnType.toString());
        System.out.printf(indentation+"- name: %s\n", identifier.toString());
        declarator.prettyPrint(indentation);

    }

    @Override
    public boolean equals(Object o) {
        Procedure p = (Procedure) o;

        if(!this.returnType.equals(p.returnType)) return false;
        else if(!this.identifier.equals(p.identifier)) return false;
        else if(!this.declarator.equals(p.declarator)) return false;
        
        return true;
    }
}
class ProcedureDeclarator extends Statement {
    ArrayList<Expression> parameters;
    Block block;

    protected ProcedureDeclarator(int line, ArrayList<Expression> parameters, Block block) {
        super(line);
        this.parameters = parameters;
        this.block = block;
    }

    @Override
    public void printNode() {
        System.out.print("     - Parameters: ");
        for(Expression p: parameters){
            System.out.printf(" %s ",p.getRep());
        }
        System.out.print("\n     - Block: ");
        block.printNode();

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
}
class Parameter extends Expression {
    TypeDeclaration type;
    Expression expression;
    protected Parameter(int line, TypeDeclaration type, Expression expression) {
        super(line);
        this.type = type;
        this.expression = expression;
    }

    @Override
    public String getRep() {
        return type.toString() + " " +  expression.getRep();
    }

    @Override
    public void printNode() {

    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"- type:   %s\n",type);
        System.out.print(indentation+"- name:");
        expression.prettyPrint(indentation);

    }

    @Override
    public String toString() {
        return "Param{ " +
                type +
                ", " + expression +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        Parameter p = (Parameter) o;

        if(!this.type.equals(p.type)) return false;
        else if(!this.expression.equals(p.expression)) return false;
        
        return true;
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

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("Block{");
        for(Statement s : statements){
            ret.append(s.toString());
        }
        return ret.toString();
    }

    @Override
    public void prettyPrint(String indentation) {
        for(Statement s : statements){
            s.prettyPrint(indentation+"  ");
        }
    }
    @Override
    public boolean equals(Object o) {
        return this.statements.equals(((Block) o ).statements);
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

    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"If: %s\n", ifCondition);
        ifBlock.prettyPrint(indentation+"  ");
        System.out.print(indentation+"Else:\n");
        elseBlock.prettyPrint(indentation+"  ");

    }

    @Override
    public boolean equals(Object o) {
        IfElseStatement i = (IfElseStatement) o;

        if (!this.ifCondition.equals(i.ifCondition)) return false;
        else if (!this.ifBlock.equals(i.ifBlock)) return false;
        else if (!this.elseBlock.equals(i.elseBlock)) return false;

        return true;
    }
    public String toString() {
        return "IfElseStatement{" +
                "ifCondition=" + ifCondition +
                ",ifBlock=" + ifBlock +
                ",elseBlock=" + elseBlock +
                "}";
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

    @Override
    public void prettyPrint(String indentation) {
        System.out.println(indentation+"While Statement");
        System.out.println(indentation+"  - While condition:");
        condition.prettyPrint(indentation+"    ");
        System.out.println(indentation+"  - While block:");
        block.prettyPrint(indentation+"     ");

    }

    @Override
    public String toString() {
        return "WhileStatement{ " +
                "( " + condition + " )"+
                "," + block +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        WhileStatement w = (WhileStatement) o;

        if(!this.condition.equals(w.condition)) return false;
        else if(!this.block.equals(w.block)) return false;
        
        return true;
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

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Return statement:\n");
        expression.prettyPrint(indentation+"  ");

    }

    @Override
    public String toString() {
        return "ReturnStatement{ " +
                 expression +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        ReturnStatement r = (ReturnStatement) o;

        if(!this.expression.equals(r.expression)) return false;
        
        return true;
    }
}

