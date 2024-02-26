package compiler.Lexer;

import java.util.Objects;

enum Token {
    EOF(""),
    // IDENTIFIERS
    INTEGER("int"), FLOAT("float"), FREE("free"),
    BOOLEAN("bool"), STRING("string"), IDENTIFIER("<IDENTIFIER>"),

    // OPERATORS
    PLUS("+"), MINUS("-"), STAR("*"),
    SLASH("/"), EQUAL("=="),
    ASSIGN("="), NEGATE("!"), NOT_EQUAL("!="), LT("<"), GT(">"),
    LE("<="), GE(">="), MODULO("%"), LAND("&&"),
    LOR("||"),

    // KEYWORDS
    FINAL("final"), STRUCT("struct"), DEF("def"), FOR("for"),
    WHILE("while"), IF("if"), ELSE("else"), RETURN("return"),

    // LITERALS
    STRING_LITERAL("<STRING_LITERAL>"), NATURAL_LITERAL("<NATURAL_LITERAL>"), FLOAT_LITERAL("<FLOAT_LITERAL>"),
    BOOLEAN_LITERAL("<BOOLEAN_LITERAL>"),

    //SPECIAL SYMBOLS
    LCURLY("{"), RCURLY("}"), LPARAN("("),RPARAN(")"),
    SEMI_COLON(";"), DOT("."), LSQUARE("["), RSQUARE("]"),
    COMMA(",");

    private String image;

    private Token(String image) {
        this.image = image;
    }

    public String symbolRep() {
        if (this == EOF) {
            return "<EOF>";
        }
        if (image.startsWith("<") && image.endsWith(">")) {
            return image ;
        }
        return "\"" + image + "\"";
    }

    public String image() {
        return image;
    }
}

public class Symbol {
    private Token token;
    private String attribute;
    private int line;

    public Symbol(Token kind, String attribute, int line) {
        this.token = kind;
        this.attribute = attribute;
        this.line = line;
    }

    public Symbol(Token kind, int line) {
        this(kind, kind.image(), line);
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
