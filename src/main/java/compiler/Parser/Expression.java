package compiler.Parser;

import compiler.Lexer.Symbol;
import compiler.Lexer.Token;

import java.util.ArrayList;

public abstract class Expression extends Statement{
    Expression lhs;
    Expression rhs;
    String operator;
    public Expression(int line, Expression lhs, Expression rhs, String operator) {
        super(line);
        this.lhs = lhs;
        this.rhs = rhs;
        this.operator = operator;
    }

    public Expression(int line) {
        super(line);
    }

    public abstract String getRep();

    public GenericType getType(){
        GenericType tl = this.lhs.getType();
        GenericType tr = this.rhs.getType();
        if(tl.type().equals(tr.type())){
            return tl;
        } else if (tl.type().equals(Token.FLOAT.image()) && tr.type().equals(Token.INTEGER.image())) {
            return tl;
        } else if (tr.type().equals(Token.FLOAT.image()) && tl.type().equals(Token.INTEGER.image())) {
            return tr;
        }
        return new Type("With attempted type of:" + lhs.getType() + " "+ operator+" "+  rhs.getType(), false);
    }
    @Override
    public String toString() {
        return  lhs + " '" + operator +"' "+ rhs;
    }

    @Override
    public void prettyPrint(String indentation) {
        if(lhs != null){
            System.out.printf(indentation+"  LHS:\n");
            lhs.prettyPrint(indentation+"   ");
        }
        if(rhs != null){
            System.out.printf(indentation+"  RHS:\n");
            rhs.prettyPrint(indentation+"   ");
        }


    }
}
/*--------------------------------------------------------------------------------------------------------------------*/
/** LOGICAL EXPRESSION:
 *             logicalExpression -> equalityExpression ((LAND | LOR ) logicalExpression)*
 * */
abstract  class LogicalExpression extends Expression{
    public LogicalExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public GenericType getType() {
        GenericType before_type = super.getType();
        if(before_type.type().contains("With")){
            return before_type;
        }else{
            return new Type(Token.BOOLEAN.image(), false);
        }
    }

    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitLogicalExpression(this);
    }
}
class LogicalAnd extends LogicalExpression{
    public LogicalAnd(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "&&");
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"AND op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        LogicalAnd a = (LogicalAnd) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
class LogicalOr extends LogicalExpression{
    public LogicalOr(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "||");
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"OR op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        LogicalOr a = (LogicalOr) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
/*--------------------------------------------------------------------------------------------------------------------*/
/** EQUALITY EXPRESSION:
 *             equalityExpression -> comparisonExpression ( ( NOT_EQUAL | EQUAL ) comparisonExpression  )*
 * */
abstract class EqualityCheckExpression extends Expression{

    public EqualityCheckExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public GenericType getType() {
        GenericType before_type = super.getType();
        if(before_type.type().equals("ERROR")){
            return before_type;
        }else{
            return new Type(Token.BOOLEAN.image(),false);
        }
    }


    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitEqualityCheckExpression(this);
    }
}
class NotEqualComparison extends EqualityCheckExpression {

    public NotEqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!=");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Not equal comp:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        NotEqualComparison a = (NotEqualComparison) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
class EqualComparison extends EqualityCheckExpression {

    public EqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "==");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Equality comp:\n");
        super.prettyPrint(indentation+" ");
    }

    @Override
    public boolean equals(Object o) {
        EqualComparison a = (EqualComparison) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
/*--------------------------------------------------------------------------------------------------------------------*/
/** COMPARISON EXPRESSION:
 *              comparisonExpression -> termExpression ( ( LT | GT | LE | GE ) termExpression )*
 * */
abstract class ComparisionExpression extends Expression {
    public ComparisionExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }


    @Override
    public GenericType getType() {
        GenericType before_type = super.getType();
        if(before_type.type().equals("ERROR")){
            return before_type;
        }else{
            return new Type(Token.BOOLEAN.image(),false);
        }
    }

    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitComparisonExpression(this);
    }
}
class LTComparison extends ComparisionExpression{
    public LTComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "<");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"LT comp:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        LTComparison a = (LTComparison) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
class GTComparison extends ComparisionExpression{
    public GTComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ">");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"GT comp:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        GTComparison a = (GTComparison) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
class LEComparison extends ComparisionExpression{
    public LEComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "<=");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"LE comp:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        LEComparison a = (LEComparison) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
class GEComparison extends ComparisionExpression{
    public GEComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ">=");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"GE comp:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        GEComparison a = (GEComparison) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
/*--------------------------------------------------------------------------------------------------------------------*/
/** TERM EXPRESSION:
 *              termExpression -> unaryExpression ( ( MINUS | PLUS ) termExpression  )*
 * */
abstract class TermExpression extends Expression{
    public TermExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }
    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }



    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitTermExpression(this);

    }
}
class MinusOperation extends TermExpression{
    public MinusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "-");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Minus op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        MinusOperation a = (MinusOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }

}
class PlusOperation extends TermExpression{
    public PlusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "+");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Plus op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        PlusOperation a = (PlusOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }

}
/*--------------------------------------------------------------------------------------------------------------------*/
/** UNARY EXPRESSION:
 *              unaryExpression -> ( NEGATE | MINUS ) unaryExpression | factorExpression
 * */
abstract class UnaryExpression extends Expression{
    public UnaryExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }
    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public GenericType getType() {
        assert lhs == null;
        return rhs.getType();
    }

}
class UnaryNegateOperation extends UnaryExpression{
    public UnaryNegateOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!");
    }



    @Override
    public boolean equals(Object o) {
        UnaryNegateOperation a = (UnaryNegateOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitUnaryNegateExpression(this);

    }
    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Unary Negate op:\n");
        super.prettyPrint(indentation+" ");
    }
}
class UnaryMinusOperation extends UnaryExpression{
    public UnaryMinusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "-");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Unary minus op:\n");
        super.prettyPrint(indentation+" ");
    }

    @Override
    public boolean equals(Object o) {
        UnaryMinusOperation a = (UnaryMinusOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitUnaryMinusExpression(this);

    }
}
/*--------------------------------------------------------------------------------------------------------------------*/
/** FACTOR EXPRESSION:
*               factorExpression -> primary ( ( STAR | SLASH | MODULO ) factorExpression )*
* */
abstract class FactorExpression extends Expression{
    public FactorExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitFactorExpression(this);

    }
}
class MultiplyOperation extends FactorExpression{
    public MultiplyOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "*");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Multiply op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        MultiplyOperation a = (MultiplyOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}

class DivideOperation extends FactorExpression{
    public DivideOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "/");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Division:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        DivideOperation a = (DivideOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
class ModuloOperation extends FactorExpression{
    public ModuloOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "%");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Modulo op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        ModuloOperation a = (ModuloOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}
/*--------------------------------------------------------------------------------------------------------------------*/
/**
 * PRIMARY EXPRESSION:
 *          parExpression -> LPARAN expression (COMMA expression)* RPARAN
 *          primary -> qualifiedIdentifier (parExpression) | literal | parExpression
 * */
abstract class PrimaryExpression extends Expression{
    public PrimaryExpression(int line) {
        super( line);
    }
    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitPrimaryExpression(this);

    }
}
class FunctionCallExpression extends PrimaryExpression{
    Symbol identifier;
    ArrayList<Expression> expressionParams;
    GenericType type;

    public FunctionCallExpression(int line, Symbol identifier,ArrayList<Expression> expressionParams ) {
        super(line);
        this.identifier= identifier;
        this.expressionParams = expressionParams;
    }

    @Override
    public void typeAnalyse(NodeVisitor v) {
        this.type = v.visitSymbolTableFunction(this);
        v.visitFunctionCallExpression(this);
    }

    @Override
    public GenericType getType() {
        return this.type;
    }

    @Override
    public String getRep() {
        System.out.printf("%s(", identifier.image());
        String string = "";
        for(Expression e : expressionParams){
            string = string.concat(e.getRep());
        }
        return string + ")";
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Function call:\n");
        System.out.printf(indentation+"  - identifier:%s\n",identifier);
        int i = 1;
        for(Expression e : expressionParams){
            System.out.printf(indentation+"  parameter %d:\n",i);
            e.prettyPrint(indentation+"     ");
            i++;
        }
        System.out.println();

    }

    @Override
    public String toString() {
        return "FunctionCallExpression{" +
                "identifier=" + identifier +
                ", expressionParams=" + expressionParams +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        FunctionCallExpression a = (FunctionCallExpression) o;
        
        if(!this.identifier.equals(a.identifier)) return false;
        else if(!this.expressionParams.equals(a.expressionParams)) return false;

        return true;
    }
}

class LiteralExpression extends PrimaryExpression{
    Symbol literal;
    public LiteralExpression(int line, Symbol literal) {
        super(line);
        this.literal = literal;
    }

    @Override
    public Type getType() {
        String type = literal.token().image();
        String t = "WEIRD THING HAPPENED IN LIT EXP";

        switch (type){
            case "<FLOAT_LITERAL>":{
                t = "float";
                return new Type(t,false);
            }
            case "<NATURAL_LITERAL>":{
                t = "int";
                return new Type(t,false);
            }
            case "<BOOLEAN_LITERAL>":{
                t = "bool";
                return new Type(t,false);
            }
            case "<STRING_LITERAL>":{
                t = "string";
                return new Type(t,false);
            }
        }
        return new Type(t,false);
    }

    @Override
    public String getRep() {
        return literal.image();
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.println(indentation+literal);
        super.prettyPrint(indentation);

    }

    @Override
    public String toString() {
        return "LiteralExp("+ literal.image() +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        return this.literal.equals(((LiteralExpression) o).literal);
    }
}
class ParanExpression extends PrimaryExpression{
    // expression size is always 1
    ArrayList<Expression> expressions;
    public ParanExpression(int line, ArrayList<Expression> expressions) {
        super(line);
        this.expressions = expressions;
    }

    @Override
    public GenericType getType() {
        assert expressions.size() == 1;
        return expressions.get(0).getType();
    }

    @Override
    public void typeAnalyse(NodeVisitor v) {
        v.visitParanExpression(this);
    }

    @Override
    public String getRep() {
        String string = "(";
        for(Expression e : expressions){
            string = string.concat(e.getRep());
        }
        return string + ")";
    }

    @Override
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);
        for(Expression e : expressions){
            e.prettyPrint(indentation);
        }


    }

    @Override
    public String toString() {
        return "ParanExps{ " +
                 expressions +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        return this.expressions.equals(((ParanExpression) o).expressions);
    }
}

/*--------------------------------------------------------------------------------------------------------------------*/
class IdentifierExpression extends Expression{
    Symbol id;
    GenericType type;
    public IdentifierExpression(int line, Symbol id) {
        super(line);
        this.id = id;
    }

    @Override
    public String getRep() {
        return id.image();
    }

    @Override
    public GenericType getType() {
        return this.type;
    }

    @Override
    public void typeAnalyse(NodeVisitor v) {
        this.type = v.visitSymbolTableIdentifier(this);
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"Identifier exp: %s\n", id);

    }


    @Override
    public String toString() {
        return "IdentifierExp{" +
                 id.image() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return this.id.equals(((IdentifierExpression) o).id);
    }
}
class IndexOp extends Expression {
    Symbol identifier;
    Expression index;
    GenericType type;

    public IndexOp(int line, Symbol identifier, Expression index) {
        super(line);
        this.identifier = identifier;
        this.index = index;
    }


    @Override
    public String getRep() {
        return identifier.image()+"["+index.getRep()+"]";
    }


    @Override
    public void typeAnalyse(NodeVisitor v) {
        // add a call to symboltable
        this.type = v.visitSymbolTableIndexOp(this);
        v.visitIndexOperation(this);
    }

    @Override
    public GenericType getType() {
        return type;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Index op:\n");
        System.out.printf(indentation+"- identifier: %s\n", identifier);
        System.out.printf(indentation+"- index:\n");
        index.prettyPrint(indentation+"  ");


    }

    @Override
    public String toString() {
        return "IndexOp{" +
                "identifier=" + identifier +
                ", index=" + index +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        IndexOp a = (IndexOp) o;
        
        if(!this.identifier.equals(a.identifier)) return false;
        else if(!this.index.equals(a.index)) return false;

        return true;
    }
}

class DotOperation extends Expression{
    GenericType type;

    public DotOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ".");
    }



    @Override
    public String getRep() {
        return null;
    }

    @Override
    public void typeAnalyse(NodeVisitor v) {
        this.type = v.visitSymbolTableDotOp(this);
        v.visitDotOperation(this);
    }

    @Override
    public GenericType getType() {
        return type;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Dot op:\n");
        super.prettyPrint(indentation+" ");

    }

    @Override
    public boolean equals(Object o) {
        DotOperation a = (DotOperation) o;
        
        if(!this.lhs.equals(a.lhs)) return false;
        else if(!this.rhs.equals(a.rhs)) return false;
        else if (!this.operator.equals(a.operator)) return false;

        return true;
    }
}

