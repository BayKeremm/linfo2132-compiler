package compiler.Parser;

import compiler.Lexer.Symbol;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.Type;

public class LiteralExpression extends PrimaryExpression{
    public Symbol getLiteral() {
        return literal;
    }

    Symbol literal;
    public LiteralExpression(int line, Symbol literal) {
        super(line);
        this.literal = literal;
    }

    @Override
    public Type getType() {
        String type = literal.token().image();
        String t = "WEIRD THING HAPPENED IN LIT EXP";

        switch (type){
            case "<FLOAT_LITERAL>":{
                t = "float";
                return new Type(t,false);
            }
            case "<NATURAL_LITERAL>":{
                t = "int";
                return new Type(t,false);
            }
            case "<BOOLEAN_LITERAL>":{
                t = "bool";
                return new Type(t,false);
            }
            case "<STRING_LITERAL>":{
                t = "string";
                return new Type(t,false);
            }
        }
        return new Type(t,false);
    }

    @Override
    public String getRep() {
        return literal.image();
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.println(indentation+literal);
        super.prettyPrint(indentation);

    }

    @Override
    public String toString() {
        return "LiteralExp("+ literal.image() +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        return this.literal.equals(((LiteralExpression) o).literal);
    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitLiteral(this);

    }
}
