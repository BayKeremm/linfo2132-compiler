package compiler.Lexer;
import java.io.*;
import java.util.Hashtable;

import static compiler.Lexer.Token.*;
import static java.lang.Character.isDigit;

public class Lexer {

    private Symbol lookaheadSymbol;
    private Symbol currentSymbol;

    // Source file name.
    private String fileName;

    // Next character to lex
    private char ch;

    final private LineNumberReader lineNumberReader;

    // Line number of current Symbol.
    private int line;

    private final Hashtable<String, Token> keywords;

    public final static char EOF_CHAR = (char) -1;

    public Lexer(Reader reader) throws Exception {
        this.lineNumberReader = (LineNumberReader) reader;

        keywords = new Hashtable<String, Token>();
        keywords.put(INTEGER.image(), INTEGER);
        keywords.put(FLOAT.image(), FLOAT);
        keywords.put(BOOLEAN.image(),BOOLEAN);
        keywords.put(STRING.image(),STRING);
        keywords.put(FREE.image(),FREE);

        keywords.put(FINAL.image(),FINAL);
        keywords.put(STRUCT.image(),STRUCT);
        keywords.put(DEF.image(),DEF);
        keywords.put(FOR.image(),FOR);
        keywords.put(WHILE.image(),WHILE);
        keywords.put(IF.image(),IF);
        keywords.put(ELSE.image(),ELSE);
        keywords.put(RETURN.image(),RETURN);


        nextCh();
    }
    public void finish() throws IOException {
        this.lineNumberReader.close();
    }
    public void setFileName(String name){
        this.fileName = name;
    }

    public void advanceLexer() throws Exception{
        if(this.currentSymbol == null){
            this.currentSymbol = getNextSymbol();
        }else{
            this.currentSymbol = this.lookaheadSymbol;
        }
        this.lookaheadSymbol = getNextSymbol();
    }
    public Symbol nextSymbol(){
        return this.currentSymbol;
    }

    public Symbol lookaheadSymbol(){
        return this.lookaheadSymbol;
    }


    private Symbol getNextSymbol() throws Exception {
        StringBuilder buffer;
        boolean moreWhiteSpace = true;

        while (moreWhiteSpace) {
            // RE = (' ' | '\t' | '\n' | '\f')*
            while (isWhitespace(ch)) {
                nextCh();
            }
           // RE = (//)([a-zA-Z]|[0-9])*
            if (ch == '/') {
                nextCh();
                if (ch == '/') {
                    while (ch != '\n' && ch != EOF_CHAR) {
                        nextCh();
                    }
                } else { // RE = /
                    nextCh();
                    return new Symbol(SLASH, this.lineNumber());
                }
            }else{
                moreWhiteSpace = false;
            }
        }
        line = this.lineNumber();
        switch(ch){
            case '(':
                nextCh();
                return new Symbol(LPARAN,line);
            case ')':
                nextCh();
                return new Symbol(RPARAN,line);
            case '{':
                nextCh();
                return new Symbol(LCURLY,line);
            case '}':
                nextCh();
                return new Symbol(RCURLY,line);
            case '[':
                nextCh();
                return new Symbol(LSQUARE,line);
            case ']':
                nextCh();
                return new Symbol(RSQUARE,line);
            case ';':
                nextCh();
                return new Symbol(SEMI_COLON,line);
            case '.':
                nextCh();
                return new Symbol(DOT,line);
            case ',':
                nextCh();
                return new Symbol(COMMA,line);
            case '+': // RE = +
                nextCh();
                return new Symbol(PLUS,line);
            case '-': // RE = -
                nextCh();
                return new Symbol(MINUS,line);
            case '*': // RE = *
                nextCh();
                return new Symbol(STAR,line);
            case '=': // RE = (= | ==)
                nextCh();
                if(ch == '='){
                    nextCh();
                    return new Symbol(EQUAL,line);
                }else{
                    return new Symbol(ASSIGN,line);
                }
            case '!': // RE = (! | !=)
                nextCh();
                if(ch == '='){
                    nextCh();
                    return new Symbol(NOT_EQUAL,line);
                }else{
                    return new Symbol(NEGATE,line);
                }
            case '<': // RE = (< | <=)
                nextCh();
                if(ch == '='){
                    nextCh();
                    return new Symbol(LE,line);
                }else{
                    return new Symbol(LT,line);
                }
            case '>': // RE = (> | >=)
                nextCh();
                if(ch == '='){
                    nextCh();
                    return new Symbol(GE,line);
                }else{
                    return new Symbol(GT,line);
                }
            case '%': // RE = %
                nextCh();
                return new Symbol(MODULO,line);
            case '&': // RE = &&
                nextCh();
                if(ch == '&'){
                    nextCh();
                    return new Symbol(LAND,line);
                }else{
                    reportLexerError("Operator & is not supported in lang",true);
                    return getNextSymbol();
                }
            case '|': // RE = ||
                nextCh();
                if(ch == '|'){
                    nextCh();
                    return new Symbol(LOR,line);
                }else{
                    reportLexerError("Operator | is not supported in lang",true);
                    return getNextSymbol();
                }
            case '"': // RE = "(any char)*"
                buffer = new StringBuilder();
                buffer.append('\"');
                nextCh();
                while(ch != '\n' && ch != EOF_CHAR && ch != '\"'){
                    buffer.append(ch);
                    nextCh();
                }
                if(ch == '\n'){
                    reportLexerError("Unexpected new line in string literal",true);
                    return getNextSymbol();
                }else if(ch == EOF_CHAR){
                    reportLexerError("Unexpected EOF in string literal",true);
                    return getNextSymbol();
                }else {
                    buffer.append('\"');
                    nextCh();
                }
                String string = buffer.toString();
                return new Symbol(STRING_LITERAL,string,line);

            case EOF_CHAR: // RE = ""
                return new Symbol(EOF, line);
            default: // RE = ([a-zA-Z]|_)+([0-9]*) | keywords
                if(isIdentifierStart(ch)){
                    buffer = new StringBuilder();
                    while(isIdentifierPart(ch)){
                        buffer.append(ch);
                        nextCh();
                    }
                    String identifier = buffer.toString();
                    if(keywords.containsKey(identifier)){
                        return new Symbol(keywords.get(identifier),line);
                    }else if(identifier.equals("true") || identifier.equals("false")){
                        return new Symbol(BOOLEAN_LITERAL, identifier, line);
                    }else{
                        return new Symbol(IDENTIFIER, identifier, line);
                    }
                }else if(isDigit(ch)){ // RE = ([0-9])*+[.]+([0-9])*
                    buffer = new StringBuilder();
                    while(isDigit(ch)){
                        buffer.append(ch);
                        nextCh();
                    }if(ch == '.'){
                        buffer.append(ch);
                        nextCh();
                        while(isDigit(ch)){
                            buffer.append(ch);
                            nextCh();
                        }
                        if (ch == '.'){
                            while(ch == '.' || isDigit(ch)){
                                buffer.append(ch);
                                nextCh();
                            }
                            reportLexerError("Invalid float %s",true, buffer.toString());
                            return getNextSymbol();
                        }
                        return new Symbol(FLOAT_LITERAL, buffer.toString(), line);
                    }else if(isIdentifierPart(ch)){
                        while(isIdentifierPart(ch)){
                            buffer.append(ch);
                            nextCh();
                        }
                        reportLexerError("Invalid identifier %s",true, buffer.toString());
                        return getNextSymbol();
                    }
                    return new Symbol(NATURAL_LITERAL, buffer.toString(), line);
                
                }else{
                    reportLexerError("Unidentified input char %s",true, ch);
                    nextCh();
                    return getNextSymbol();
                }
        }
    }
    private void nextCh() throws Exception {
        line = this.lineNumber();
        try {
            ch = (char) lineNumberReader.read();
        } catch (Exception e) {
            reportLexerError("Unable to read characters from input",true);
        }
    }
    private int lineNumber(){
       return  lineNumberReader.getLineNumber() + 1;
    }

    private void reportLexerError(String message, Boolean throw_exception, Object... args) throws Exception {
        System.out.println("DECIDE WHAT TO DO IN ERROR");
        System.err.printf("%s:%d: error: ", fileName, line);
        System.err.printf(message, args);
        System.err.println();
        if(throw_exception){
            throw new Exception("Lexing error");
        }
    }

    private boolean isWhitespace(char c) {
        return (c == ' ' || c == '\t' || c == '\n' || c == '\f');
    }
    private boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_');
    }
    private boolean isIdentifierPart(char c) {
        return (isIdentifierStart(c) || isDigit(c));
    }
}