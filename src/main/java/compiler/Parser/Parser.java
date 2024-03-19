package compiler.Parser;

import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

import java.util.ArrayList;
import java.util.Objects;


public class Parser {
    private Lexer lexer;
    private Symbol nextSymbol;
    private ArrayList<String> types;
    private ArrayList<Token> literals;
    static private final String ANSI_RED = "\u001B[31m";
    static private final String ANSI_RESET = "\u001B[0m";

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        lexer.advanceLexer();
        this.nextSymbol = lexer.nextSymbol();
        types = new ArrayList<>();
        types.add(Token.INTEGER.image());
        types.add(Token.FLOAT.image());
        types.add(Token.STRING.image());
        types.add(Token.BOOLEAN.image());

        this.literals = new ArrayList<>();
        literals.add(Token.BOOLEAN_LITERAL);
        literals.add(Token.FLOAT_LITERAL);
        literals.add(Token.STRING_LITERAL);
        literals.add(Token.NATURAL_LITERAL);
    }
    public Program program()  {
        // Parse constants
        ArrayList<ConstantVariable> constantVariables = new ArrayList<>();
        ArrayList<StructDeclaration> structs = new ArrayList<>();
        ArrayList<Procedure> procedures = new ArrayList<>();
        ArrayList<Statement> globals = new ArrayList<>();
        while(have(Token.FINAL)){
            int line = nextSymbol.line();
            TypeDeclaration type = type();
            Expression identifier = expression();
            mustbe(Token.ASSIGN);
            Expression declarator = expression();
            mustbe(Token.SEMI_COLON);
            constantVariables.add(new ConstantVariable(line,type,identifier, declarator));
        }
        // Parse structs
        while(have(Token.STRUCT)){
            int line = nextSymbol.line();
            types.add(nextSymbol.image());
            Expression identifier = expression();
            Block block = block();
            structs.add(new StructDeclaration(line,identifier,block));
        }
        // Parse Globals
        while(isType()){
            int line = nextSymbol.line();
            TypeDeclaration type = type();
            Expression idExpression = expression();
            if(have(Token.ASSIGN)){
                Expression declarator = expression();
                globals.add(new Variable(line, type, idExpression,declarator));
                have(Token.SEMI_COLON);
                continue;
            }
            globals.add(new UninitVariable(line, type, idExpression));
            have(Token.SEMI_COLON);
        }

        // Parse procedures
        while(have(Token.DEF)){
            int line = nextSymbol.line();
            TypeDeclaration type = type();
            Symbol identifier = qualifiedIdentifier();
            mustbe(Token.LPARAN);
            ArrayList<Expression> params = parseParameters();
            Block block = block();
            ProcedureDeclarator dec = new ProcedureDeclarator(line,params,block);
            procedures.add(new Procedure(line,dec,type, identifier));
        }

        if(have(Token.EOF)){
            System.out.println("That's rough buddy");
        }

        return new Program(lexer.getFileName(),constantVariables,globals, structs, procedures);
    }
    private Block block(){
        boolean start = have(Token.LCURLY);
        int blockLine = nextSymbol.line();
        ArrayList<Statement> statements = new ArrayList<>();
        while(!have(Token.RCURLY )&& start){
            int line = nextSymbol.line();
            if(have(Token.IF)){
                mustbe(Token.LPARAN);
                Expression ifCondition = expression();
                mustbe(Token.RPARAN);
                Block ifBlock = block();
                if(have(Token.ELSE)){
                    Block elseBlock = block();
                    statements.add(new IfElseStatement(line,ifCondition,ifBlock,elseBlock));
                    continue;
                }
                statements.add(new IfElseStatement(line,ifCondition,ifBlock,null));
            }else if(have(Token.WHILE)){
                mustbe(Token.LPARAN);
                Expression whileCondition = expression();
                mustbe(Token.RPARAN);
                Block whileBlock = block();
                statements.add(new WhileStatement(line,whileCondition,whileBlock));
            }else if(have(Token.FOR)){
                mustbe(Token.LPARAN);
                Statement p0 = statement();
                mustbe(Token.COMMA);
                Expression p1 = expression();
                mustbe(Token.COMMA);
                Statement p2 = statement();
                mustbe(Token.RPARAN);
                statements.add(new ForStatement(line,p0,p1,p2,block()));
            }else if(have(Token.RETURN)){
                Expression exp = expression();
                statements.add(new ReturnStatement(line,exp));
                mustbe(Token.SEMI_COLON);
            }else{
                statements.add(statement());
                mustbe(Token.SEMI_COLON);
            }
        }
        return new Block(blockLine,statements);
    }
    private ArrayList<Expression> parseParameters(){
        ArrayList<Expression> params = new ArrayList<>();
        boolean more = true;
        while(!have(Token.RPARAN) && more){
            int line = nextSymbol.line();
            TypeDeclaration type = type();
            Expression e = expression();
            params.add(new Parameter(line,type,e));
            if(!have(Token.COMMA)){
                more = false;
            }
        }
        return params;
    }
    private Statement statement(){
        return assignmentStatement();
    }
    private Statement assignmentStatement(){
        int line = nextSymbol.line();
        if(isType()){
            TypeDeclaration type = type();
            Expression idExpression = expression();
            if(have(Token.ASSIGN)){
                Expression declarator = expression();
                return new Variable(line,type,idExpression,declarator);
            }
            return new UninitVariable(line,type,idExpression);
        }else{
            Expression expression = expression();
            if(have(Token.ASSIGN)){
                Expression declarator = expression();
                return new ScopeVariable(line,expression,declarator);
            }
            return expression;
        }
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
    private Expression primary(){
        int line = nextSymbol.line();
        if(have(Token.LPARAN)){
            return new ParanExpression(line,parseExpressions());
        }else if(check(Token.IDENTIFIER)){
            Symbol id = qualifiedIdentifier();
            if(have(Token.LPARAN)){
                return new FunctionCallExpression(line,id,parseExpressions());
            }else if(have(Token.LBRAC)){
                Expression index = expression();
                mustbe(Token.RBRAC);
                if(have(Token.DOT)){
                    Expression afterDot = expression();
                    return new DotOperation(line,new IndexExpression(line, id, index),afterDot );
                }
                return new IndexExpression(line, id, index);
            }else if(have(Token.DOT)){
                Expression afterDot = expression();
                return new DotOperation(line,new IdentifierExpression(line,id), afterDot);
            }
            else{
                return new IdentifierExpression(line,id);
            }
        } else if(isType()){
            TypeDeclaration type = type();
            Expression size = expression();
            mustbe(Token.RBRAC);
            return new ArrayInitializer(line,type,size);
        }
        else{
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
    private ArrayList<Expression> parseExpressions(){
        ArrayList<Expression> expressions = new ArrayList<>();
        boolean more = true;
        while(!have(Token.RPARAN) && more ){
            expressions.add(expression());
            if(!have(Token.COMMA)){
                more = false;
            }
        }
        return expressions;
    }
    private Symbol qualifiedIdentifier() {
        return match(Token.IDENTIFIER);
    }
    private boolean isArrayType(){
        if(have(Token.LBRAC)){
            if(check(Token.RBRAC)){
                return have(Token.RBRAC);
            }else{
                return true;
            }
        }
        return false;
    }
    private TypeDeclaration type()  {
        int line = nextSymbol.line();
        Symbol type = match(nextSymbol.token());
        if(isArrayType()){
            return new TypeDeclaration(line,type,true);
        }
        return new TypeDeclaration(line, type,false);
    }
    private Boolean isType(){
        for(String image : types){
            if(Objects.equals(image, nextSymbol.image())){
                return true;
            }
        }
        return false;
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
    private void mustbe(Token token){
        if(check(token)){
            match(token);
        }else{
            // Report Error
            reportParserError(" -> Token mismatch error -> expected: %s", token);
        }
    }

    private void reportParserError(String message, Object... args)  {
        System.err.print(ANSI_RED);
        System.err.printf("Parser error: %s:%d ", lexer.getFileName(), nextSymbol.line());
        System.err.printf(message, args[0]);
        System.err.printf(" got: %s\n", nextSymbol);
        System.err.println("Continuing parsing ...\n ");
        System.err.print(ANSI_RESET);
        System.exit(1);
    }private Boolean check(Token token){
        return nextSymbol.token() == token;
    }

}
