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
    /*
    private VarDeclarator declarator(){
        int line = nextSymbol.line();
        Expression expression = expression();
        have(Token.SEMI_COLON);
        return new VarDeclarator(line,expression);

    }

     */
    public Program program()  {
        // Parse constants
        ArrayList<ConstantVariable> constantVariables = new ArrayList<>();
        ArrayList<Procedure> procedures = new ArrayList<>();
        while(have(Token.FINAL)){
            int line = nextSymbol.line();
            Symbol type = type();
            Expression identifier = expression();
            have(Token.ASSIGN);
            Expression declarator = expression();
            have(Token.SEMI_COLON);
            constantVariables.add(new ConstantVariable(line,type,identifier, declarator));
        }
        // Parse procedures
        while(have(Token.DEF)){
            int line = nextSymbol.line();
            Symbol type = type();
            Symbol identifier = qualifiedIdentifier();
            if(!have(Token.LPARAN)){
                // problem
            }
            ArrayList<Parameter> params = parseParameters();
            Block block = block();
            ProcedureDeclarator dec = new ProcedureDeclarator(line,params,block);
            procedures.add(new Procedure(line,dec,type, identifier));
        }

        if(have(Token.EOF)){
            System.out.println("That's rough buddy");
        }

        return new Program(lexer.getFileName(),constantVariables, procedures);
    }
    private Block block(){
        boolean start = have(Token.LCURLY);
        int blockLine = nextSymbol.line();
        ArrayList<Statement> statements = new ArrayList<>();
        while(!have(Token.RCURLY )&& start){
            int line = nextSymbol.line();
            if(have(Token.IF)){
                have(Token.LPARAN);
                Expression ifCondition = expression();
                have(Token.RPARAN);
                Block ifBlock = block();
                if(have(Token.ELSE)){
                    Block elseBlock = block();
                    statements.add(new IfElseStatement(line,ifCondition,ifBlock,elseBlock));
                    continue;
                }
                statements.add(new IfElseStatement(line,ifCondition,ifBlock,null));
            }else if(have(Token.WHILE)){
                have(Token.LPARAN);
                Expression whileCondition = expression();
                have(Token.RPARAN);
                Block whileBlock = block();
                statements.add(new WhileStatement(line,whileCondition,whileBlock));
            }else if(have(Token.FOR)){
                have(Token.LPARAN);
                Statement p0 = statement();
                have(Token.COMMA);
                Expression p1 = expression();
                have(Token.COMMA);
                Statement p2 = statement();
                have(Token.RPARAN);
                statements.add(new ForStatement(line,p0,p1,p2,block()));
            }else if(have(Token.RETURN)){
                Expression exp = expression();
                statements.add(new ReturnStatement(line,exp));
                have(Token.SEMI_COLON);
            }else{
                statements.add(statement());
            }

        }

        return new Block(blockLine,statements);
    }
    private ArrayList<Parameter> parseParameters(){
        ArrayList<Parameter> params = new ArrayList<>();
        boolean more = true;
        while(!have(Token.RPARAN) && more){
            int line = nextSymbol.line();
            Symbol type = type();
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
            Symbol type = type();
            Expression idExpression = expression();
            if(have(Token.ASSIGN)){
                Expression declarator = expression();
                have(Token.SEMI_COLON);
                return new LocalVariable(line,type,idExpression,declarator);
            }
            have(Token.SEMI_COLON);
            return new UninitVariable(line,type,idExpression);
        }else{
            Expression expression = expression();
            if(have(Token.ASSIGN)){
                Expression declarator = expression();
                have(Token.SEMI_COLON);
                return new ScopeVariable(line,expression,declarator);
            }
            have(Token.SEMI_COLON);
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
            }else{
                return new IdentifierExpression(line,id);
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
    private ArrayList<Expression> parseExpressions(){
        // TODO: CHECK A BIT SHADY
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
        //TODO: DOT
        return match(Token.IDENTIFIER);
    }
    private Symbol type()  {
        if(isType()){
            return match(nextSymbol.token());
        }else if(check(Token.IDENTIFIER)){
            return match(nextSymbol.token());
        }else{
            return null;
        }
    }
    private Boolean isType(){
        // TODO: Add check for struct types defined before as well
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
