package compiler.Parser.statements;

import compiler.Parser.expressions.Expression;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class StructDeclaration extends Statement {
    Expression identifier;

    public Expression getStructIdentifier() {
        return identifier;
    }

    public Block getStructBlock() {
        return block;
    }

    Block block;

    public StructDeclaration(int line, Expression identifier, Block block) {
        super(line);
        this.identifier = identifier;
        this.block = block;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.println("Struct Declaration: ");
        System.out.printf(indentation+"- identifier: %s\n",identifier.toString());
        //System.out.printf(indentation+"- block: %s\n",block.toString());

        block.prettyPrint(indentation+" ");


    }

    @Override
    public boolean equals(Object o) {
        StructDeclaration s = (StructDeclaration) o;

        if(!this.block.equals(s.block)) return false;
        else if(!this.identifier.equals(s.identifier)) return false;
        
        return true;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitStructDeclaration(this);
    }

    @Override
    public void codeGen(ByteVisitor b) {

    }
}
