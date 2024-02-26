package compiler.Lexer;
import javax.swing.plaf.synth.SynthButtonUI;
import java.io.*;
import java.util.Hashtable;

import static compiler.Lexer.CharReader.EOFCH;
import static compiler.Lexer.SymbolKind.*;
import static java.lang.Character.isDigit;

public class Lexer {
    // Source file name.
    private String fileName;

    // Source characters.
    private CharReader input;

    // Next unscanned character.
    private char ch;

    // Line number of current token.
    private int line;

    // identifiers in the language
    private Hashtable<String, SymbolKind> keywords;

    public Lexer(Reader reader) throws FileNotFoundException {
        this.input = new CharReader(reader);

        keywords = new Hashtable<String, SymbolKind>();
        keywords.put(INTEGER.image(), INTEGER);
        keywords.put(FLOAT.image(), FLOAT);
        keywords.put(BOOLEAN.image(),BOOLEAN);
        keywords.put(STRING.image(),STRING);

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
        this.input.close();
    }
    public void setFileName(String name){
        this.fileName = name;
    }

    public Symbol getNextSymbol() {
        StringBuffer buffer;
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
                    while (ch != '\n' && ch != EOFCH) {
                        nextCh();
                    }
                } else { // RE = /
                    nextCh();
                    return new Symbol(SLASH, input.line());
                }
            }else{
                moreWhiteSpace = false;
            }
        }
        line = input.line();
        switch(ch){
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
                    reportLexerError("Operator & is not supported in lang");
                    return getNextSymbol();
                }
            case '|': // RE = ||
                nextCh();
                if(ch == '|'){
                    nextCh();
                    return new Symbol(LOR,line);
                }else{
                    reportLexerError("Operator | is not supported in lang");
                    return getNextSymbol();
                }
            case '"': // RE = "(any char)*"
                buffer = new StringBuffer();
                buffer.append('\"');
                nextCh();
                while(ch != '\n' && ch != EOFCH && ch != '\"'){
                    buffer.append(ch);
                    nextCh();
                }
                if(ch == '\n'){
                    reportLexerError("Unexpected new line in string literal");
                    nextCh();
                    return getNextSymbol();
                }else if(ch == EOFCH){
                    reportLexerError("Unexpected EOF in string literal");
                    nextCh();
                    return getNextSymbol();
                }else if (ch == '\"'){
                   buffer.append('\"');
                   nextCh();
                }else{
                    reportLexerError("Unexpected error lexing a string literal");
                    nextCh();
                    return getNextSymbol();
                }
                String string = buffer.toString();
                return new Symbol(STRING_LITERAL,string,line);

            case EOFCH: // RE = ""
                return new Symbol(EOF, line);
            default: // RE = ([a-zA-Z]|_)+([0-9]*) | keywords
                if(isIdentifierStart(ch)){
                    buffer = new StringBuffer();
                    while(isIdentifierPart(ch)){
                        buffer.append(ch);
                        nextCh();
                    }
                    String identifier = buffer.toString();
                    if(keywords.containsKey(identifier)){
                        return new Symbol(keywords.get(identifier),line);
                    }else{
                        return new Symbol(IDENTIFIER, identifier, line);
                    }
                }else{
                    reportLexerError("Unidentified input char %s",ch);
                    nextCh();
                    return getNextSymbol();
                }
        }
    }
    // Advances ch to the next character from input, and updates the line number.
    private void nextCh() {
        line = input.line();
        try {
            ch = input.nextChar();
        } catch (Exception e) {
            reportLexerError("Unable to read characters from input");
        }
    }
    private void reportLexerError(String message, Object... args) {
        System.out.println("DECIDE WHAT TO DO IN ERROR");
        System.err.printf("%s:%d: error: ", fileName, line);
        System.err.printf(message, args);
        System.err.println();
    }

    public void debugChar(){
        System.out.printf("current char: %s \n",ch);
        nextCh();
    }

    // Returns true if the specified character is a whitespace, and false otherwise.
    private boolean isWhitespace(char c) {
        return (c == ' ' || c == '\t' || c == '\n' || c == '\f');
    }

    // Returns true if the specified character can start an identifier name, and false otherwise.
    private boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_');
    }
    // Returns true if the specified character can be part of an identifier name, and false
    // otherwise.
    private boolean isIdentifierPart(char c) {
        return (isIdentifierStart(c) || isDigit(c));
    }
}
class CharReader{
    // Representation of the end of file as a character.
    public final static char EOFCH = (char) -1;

    // The underlying reader records line numbers.
    final private LineNumberReader lineNumberReader;

    // Name of the file that is being read.
    private String fileName;
    public CharReader(Reader input) throws FileNotFoundException {
        this.lineNumberReader = (LineNumberReader) input;
    }
    public char nextChar() throws IOException {
        return (char) lineNumberReader.read();
    }
    public int line() {
        return lineNumberReader.getLineNumber() + 1; // LineNumberReader counts lines from 0
    }
    public void close() throws IOException {
        lineNumberReader.close();
    }

}