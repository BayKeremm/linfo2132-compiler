package compiler.Parser;


import compiler.Lexer.Symbol;

import java.util.ArrayList;

class Procedure extends Statement {
    ProcedureDeclarator declarator;
    TypeDeclaration returnType;
    Symbol identifier;

    protected Procedure(int line,ProcedureDeclarator declarator, TypeDeclaration returnType, Symbol identifier) {
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
        System.out.printf(indentation + "- Parameters: %s\n", parameters);
        System.out.printf(indentation+"- Block:\n");
        block.prettyPrint(indentation+"    ");

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

    }

    @Override
    public String toString() {
        return "Param{ " +
                type +
                ", " + expression +
                " }";
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
        //return "Block{" +
        //        "statements=" + statements +
        //        '}';
    }

    @Override
    public void prettyPrint(String indentation) {
        for(Statement s : statements){
            s.prettyPrint(indentation+"  ");
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

    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"If: %s\n", ifCondition);
        ifBlock.prettyPrint(indentation+"  ");
        System.out.print(indentation+"Else:\n");
        elseBlock.prettyPrint(indentation+"  ");

    }

    @Override
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
        condition.prettyPrint(indentation+" ");
        block.prettyPrint(indentation+" ");

    }

    @Override
    public String toString() {
        return "WhileStatement{ " +
                "( " + condition + " )"+
                "," + block +
                " }";
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
        System.out.printf(indentation+"- expressions:( %s, %s, %s )\n",pos0,pos1,pos2);
        block.prettyPrint(indentation+" ");

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
        System.out.printf(indentation+"Return statement: %s\n", expression);

    }

    @Override
    public String toString() {
        return "ReturnStatement{ " +
                 expression +
                " }";
    }
}

