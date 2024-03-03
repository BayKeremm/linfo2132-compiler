package compiler.Lexer;

import java.util.Objects;

public class Symbol {
    private Token token;
    private String attribute;
    private int line;

    public Symbol(Token token, String attribute, int line) {
        this.token = token;
        this.attribute = attribute;
        this.line = line;
    }

    public Symbol(Token token, int line) {
        this(token, token.image(), line);
    }

    public Token token() {
        return token;
    }

    public int line() {
        return line;
    }

    public String symbolRep() {
        String rep = token.symbolRep();
        if(!Objects.equals(this.attribute, this.token.image())){
           rep = '<' + rep + ", " + this.attribute + '>';
        }else{
            rep = '<' + rep + ", " +  '>';
        }
        return rep;
    }

    public String image() {
        return attribute;
    }
}
