package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

import java.util.ArrayList;


public class Parser {
    private Lexer lexer;
    private Symbol nextSymbol;
    private ArrayList<Token> basicTypes;
    private ArrayList<Token> literals;

    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        lexer.advanceLexer();
        this.nextSymbol = lexer.nextSymbol();
        basicTypes = new ArrayList<>();
        basicTypes.add(Token.INTEGER);
        basicTypes.add(Token.FLOAT);
        basicTypes.add(Token.STRING);
        basicTypes.add(Token.BOOLEAN);

        this.literals = new ArrayList<>();
        literals.add(Token.BOOLEAN_LITERAL);
        literals.add(Token.FLOAT_LITERAL);
        literals.add(Token.STRING_LITERAL);
        literals.add(Token.NATURAL_LITERAL);
    }
    public Program program() throws ParserException, Exception {
        ArrayList<ConstantVariable> constantVariables = new ArrayList<>();
        while(nextSymbol.token() == Token.FINAL){
            int line = nextSymbol.line();
            match(Token.FINAL);
            Symbol type = type();
            Symbol identifier = qualifiedIdentifier();
            if(!check(Token.ASSIGN)){
                // throw exception
            }
            match(Token.ASSIGN);
            Expression expression = expression();
            match(Token.SEMI_COLON);
            constantVariables.add(new ConstantVariable(line,type,identifier,expression));
        }

        return new Program(0,constantVariables,lexer.getFileName());
    }
    private Expression expression(){
        return logicalExpression();
    }
    private Expression logicalExpression(){
        int line = nextSymbol.line();
        Expression lhs = equalityExpression();
        return  lhs;
    }
    private Expression equalityExpression(){
        int line = nextSymbol.line();
        Expression lhs = comparisonExpression();
        return lhs;
    }

    private Expression comparisonExpression(){
        int line = nextSymbol.line();
        Expression lhs = termExpression();
        return lhs;
    }

    private Expression termExpression(){
        int line = nextSymbol.line();
        Expression lhs = unaryExpression();
        return lhs;
    }
    private Expression unaryExpression(){
        int line = nextSymbol.line();
        if(check(Token.MINUS)){
            return null;
        }else if(check(Token.NEGATE)){
            return null;
        }
        else{
            return factorExpression();
        }
    }
    private Expression factorExpression(){
        int line = nextSymbol.line();
        Expression lhs = primary();
        return lhs;
    }
    private Expression primary(){
        int line = nextSymbol.line();
        if(check(Token.LPARAN)){
            ArrayList<Expression> expressions = new ArrayList<>();
            while(!check(Token.RPARAN)){
                expressions.add(expression());
            }
            match(Token.RPARAN);
            return new ParanExpression(line,expressions);
        }else if(check(Token.IDENTIFIER)){
            Symbol id = qualifiedIdentifier();
            // check if function call
        }else{
            Symbol lit =  literal();
            return new LiteralExpression(line,lit);
        }
        return null;
    }
    private Symbol literal() {
        if(literals.contains(nextSymbol.token())){
            for(Token t : literals){
                if(t == nextSymbol.token()){
                    return match(t);
                }
            }
        }
        return null;
    }

    private Symbol qualifiedIdentifier() {
        return match(Token.IDENTIFIER);
    }
    private Symbol type() throws Exception {
        if(isBasicType()){
            return match(nextSymbol.token());
        }
        return null;
    }
    private Boolean isBasicType(){
        return basicTypes.contains(nextSymbol.token());
    }

    private Symbol match(Token token) {
        if(nextSymbol.token() != token){
            return null;
        }else{
            Symbol matchingSymbol = nextSymbol;
            lexer.advanceLexer();
            nextSymbol = lexer.nextSymbol();
            return matchingSymbol;
        }
    }

    private Boolean check(Token token){
        return nextSymbol.token() == token;
    }

}
