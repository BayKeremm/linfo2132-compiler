package compiler.Parser;

public class ParserException extends Throwable {
    public ParserException(String message, Object... args) {
        System.err.printf(message, args);
    }
}
