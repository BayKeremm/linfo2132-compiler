package compiler.Lexer;

public enum Token {
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
    SEMI_COLON(";"), DOT("."), LBRAC("["), RBRAC("]"),
    COMMA(","), NULL("null");

    private String image;

    private Token(String image) {
        this.image = image;
    }

    public String tokenRep() {
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
