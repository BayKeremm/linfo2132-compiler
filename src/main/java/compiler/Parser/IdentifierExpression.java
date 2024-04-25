package compiler.Parser;

import compiler.Lexer.Symbol;
import compiler.semantics.TypeVisitor;

/*--------------------------------------------------------------------------------------------------------------------*/
public class IdentifierExpression extends Expression{
    public Symbol getIdentifierSymbol() {
        return id;
    }

    Symbol id;
    GenericType type;
    public IdentifierExpression(int line, Symbol id) {
        super(line);
        this.id = id;
    }

    @Override
    public String getRep() {
        return id.image();
    }

    @Override
    public GenericType getType() {
        return this.type;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        this.type = v.visitSymbolTableIdentifier(this);
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"Identifier exp: %s\n", id);

    }


    @Override
    public String toString() {
        return "IdentifierExp{" +
                 id.image() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return this.id.equals(((IdentifierExpression) o).id);
    }
}
