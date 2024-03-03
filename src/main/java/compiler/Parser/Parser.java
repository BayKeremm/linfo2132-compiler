package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;


public class Parser {
    private Lexer lexer;
    private Symbol lookahead;

    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        lexer.advanceLexer();
        this.lookahead = lexer.nextSymbol();
    }

    public Symbol match(Token token) throws ParserException, Exception {
        System.out.println(this.lookahead.token());
        System.out.println(token);
        if(lookahead.token() != token){
            throw new ParserException("No match for token: ", token);
        }else{
            Symbol matchingSymbol = lookahead;
            lexer.advanceLexer();
            lookahead = lexer.nextSymbol();
            return matchingSymbol;
        }
    }

}
