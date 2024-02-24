package compiler.Lexer;

enum SymbolKind {
    EOF(""),
    // BASE TYPES
    INTEGER("int"), FLOAT("float"),
    BOOLEAN("bool"), STRING("string"),

    // OPERATORS
    PLUS("+"), MINUS("-"), STAR("*"),
    SLASH("/"), EQUAL("=="),
    ASSIGN("="), NEGATE("!"), NOT_EQUAL("!="), LT("<"), GT(">"),
    LE("<="), GE(">="), MODULO("%"), LAND("&&"),
    LOR("||"), IDENTIFIER("<IDENTIFIER>");

    private String image;

    private SymbolKind(String image) {
        this.image = image;
    }

    public String symbolRep() {
        if (this == EOF) {
            return "<EOF>";
        }
        if (image.startsWith("<") && image.endsWith(">")) {
            return image;
        }
        return "\"" + image + "\"";
    }

    public String image() {
        return image;
    }
}

public class Symbol {
    private SymbolKind kind;
    private String image;
    private int line;

    public Symbol(SymbolKind kind, String image, int line) {
        this.kind = kind;
        this.image = image;
        this.line = line;
    }

    public Symbol(SymbolKind kind, int line) {
        this(kind, kind.image(), line);
    }

    public SymbolKind kind() {
        return kind;
    }

    public int line() {
        return line;
    }

    public String symbolRep() {
        return kind.symbolRep();
    }

    public String image() {
        return image;
    }
}
