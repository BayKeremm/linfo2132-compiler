package compiler.Parser.expressions;

import compiler.Lexer.Symbol;
import compiler.Parser.GenericType;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class IndexOp extends Expression {
    Symbol identifier;
    Expression index;

    public Symbol getIndexIdentifier() {
        return identifier;
    }

    public Expression getIndex() {
        return index;
    }

    GenericType type;

    public IndexOp(int line, Symbol identifier, Expression index) {
        super(line);
        this.identifier = identifier;
        this.index = index;
    }


    @Override
    public String getRep() {
        return identifier.image()+"["+index.getRep()+"]";
    }


    @Override
    public void typeAnalyse(TypeVisitor v) {
        // add a call to symboltable
        this.type = v.visitSymbolTableIndexOp(this);
        v.visitIndexOperation(this);
    }

    @Override
    public GenericType getType() {
        return type;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Index op:\n");
        System.out.printf(indentation+"- identifier: %s\n", identifier);
        System.out.printf(indentation+"- index:\n");
        index.prettyPrint(indentation+"  ");


    }

    @Override
    public String toString() {
        return "IndexOp{" +
                "identifier=" + identifier +
                ", index=" + index +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        IndexOp a = (IndexOp) o;
        
        if(!this.identifier.equals(a.identifier)) return false;
        else if(!this.index.equals(a.index)) return false;

        return true;
    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitIndexOp(this);

    }
}
