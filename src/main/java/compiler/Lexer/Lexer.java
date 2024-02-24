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

    // keywords in the language
    private Hashtable<String, SymbolKind> keywords;

    public Lexer(String  fileName) throws FileNotFoundException {
        this.fileName = fileName;
        this.input = new CharReader(fileName);

        keywords = new Hashtable<String, SymbolKind>();
        keywords.put(INTEGER.image(), INTEGER);
        keywords.put(FLOAT.image(), FLOAT);
        keywords.put(BOOLEAN.image(),BOOLEAN);
        keywords.put(STRING.image(),STRING);

        nextCh();
    }
    public void finish() throws IOException {
        this.input.close();
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
            case EOFCH: // RE = ""
                return new Symbol(EOF, line);
            default: // RE = ([a-zA-Z]|_)+([0-9]*
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
    // Reports a lexical error and records the fact that an error has occurred. This fact can be
    // ascertained from the Lexer by sending it an errorHasOccurred message.
    private void reportLexerError(String message, Object... args) {
        //isInError = true;
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
    private LineNumberReader lineNumberReader;

    // Name of the file that is being read.
    private String fileName;
    public CharReader(String fileName) throws FileNotFoundException {
        lineNumberReader = new LineNumberReader(new FileReader(fileName));
        this.fileName = fileName;
    }
    public char nextChar() throws IOException {
        return (char) lineNumberReader.read();
    }
    public int line() {
        return lineNumberReader.getLineNumber() + 1; // LineNumberReader counts lines from 0
    }
    public String fileName() {
        return fileName;
    }
    public void close() throws IOException {
        lineNumberReader.close();
    }

}