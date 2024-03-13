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
        while(have(Token.FINAL)){
            int line = nextSymbol.line();
            Symbol type = type();
            Symbol identifier = qualifiedIdentifier();
            if(!have(Token.ASSIGN)){
                // throw exception
            }
            Expression expression = expression();
            if(!have(Token.SEMI_COLON)){
                // throw exception
            }
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
        if(have(Token.LAND)){
            return new LogicalAnd(line,lhs,logicalExpression());
        }else if (have(Token.LOR)){
            return new LogicalOr(line,lhs,logicalExpression());
        }else{
            return  lhs;
        }
    }
    private Expression equalityExpression(){
        int line = nextSymbol.line();
        Expression lhs = comparisonExpression();
        if(have(Token.NOT_EQUAL)){
            return new NotEqualComparison(line,lhs,comparisonExpression());
        }else if(have(Token.EQUAL)){
            return new EqualComparison(line,lhs,comparisonExpression());
        }else{
            return lhs;
        }
    }

    private Expression comparisonExpression(){
        int line = nextSymbol.line();
        Expression lhs = termExpression();
        if(have(Token.GT)){
            return new GTComparison(line,lhs,termExpression());
        }else if(have(Token.LT)){
            return new LTComparison(line,lhs,termExpression());
        } else if (have(Token.GE)) {
            return new GEComparison(line,lhs,termExpression());
        } else if (have(Token.LE)) {
            return new LEComparison(line,lhs,termExpression());
        }else{
            return lhs;
        }
    }

    private Expression termExpression(){
        int line = nextSymbol.line();
        Expression lhs = unaryExpression();
        if(have(Token.PLUS)){
            return new PlusOperation(line,lhs, termExpression());
        } else if (have(Token.MINUS)) {
            return new MinusOperation(line,lhs, termExpression());
        }else{
            return lhs;
        }
    }
    private Expression unaryExpression(){
        int line = nextSymbol.line();
        if(have(Token.MINUS)){
            return new UnaryMinusOperation(line,null,factorExpression());
        }else if(have(Token.NEGATE)){
            return new UnaryNegateOperation(line,null,factorExpression());
        }
        else{
            return factorExpression();
        }
    }
    private Expression factorExpression(){
        int line = nextSymbol.line();
        Expression lhs = primary();
        if(have(Token.STAR)){
            return new MultiplyOperation(line,lhs, factorExpression());
        }else if(have(Token.MODULO)){
            return new ModuloOperation(line,lhs, factorExpression());
        }else if(have(Token.SLASH)){
            return new DivideOperation(line,lhs, factorExpression());
        }else{
            return lhs;
        }
    }
    private ArrayList<Expression> parseExpressions(){
        ArrayList<Expression> expressions = new ArrayList<>();
        while(!have(Token.RPARAN)){
            expressions.add(expression());
        }
        return expressions;
    }
    private Expression primary(){
        int line = nextSymbol.line();
        if(have(Token.LPARAN)){
            return new ParanExpression(line,parseExpressions());
        }else if(check(Token.IDENTIFIER)){
            Symbol id = qualifiedIdentifier();
            ArrayList<Expression> expressions;
            if(have(Token.LPARAN)){
                expressions = parseExpressions();
                return new FunctionCallExpression(line,id,expressions);
            }else{
                // TODO: RETURN VARIABLE
                return null;
            }
        }else{
            Symbol lit =  literal();
            return new LiteralExpression(line,lit);
        }
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
        //TODO: DOT
        return match(Token.IDENTIFIER);
    }
    private Symbol type()  {
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
    private boolean have(Token token){
        if(check(token)){
            match(token);
            return true;
        }else{
            return false;
        }
    }

    private Boolean check(Token token){
        return nextSymbol.token() == token;
    }

}
