package compiler.Parser.statements;

import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

import java.util.ArrayList;

public class Block extends Statement {
    ArrayList<Statement> statements;

    public Block(int line, ArrayList<Statement> statements) {
        super(line);
        this.statements = statements;
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

    @Override
    public void typeAnalyse(TypeVisitor v) {

    }

    public ArrayList<Statement> getStatements(){
        return this.statements;
    }

    @Override
    public void codeGen(ByteVisitor b) {

    }
}
