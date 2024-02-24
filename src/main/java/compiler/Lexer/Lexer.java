package compiler.Lexer;
import java.io.*;

// SCANNER
public class Lexer {
    // Source file name.
    private String fileName;

    // Source characters.
    private CharReader input;

    // Next unscanned character.
    private char ch;
    // Line number of current token.
    private int line;

    public Lexer(String  fileName) throws FileNotFoundException {
        this.fileName = fileName;
        this.input = new CharReader(fileName);
        nextCh();
    }
    
    public Symbol getNextSymbol() {
        return null;
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
    // ascertained from the Scanner by sending it an errorHasOccurred message.
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