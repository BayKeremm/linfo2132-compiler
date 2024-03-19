package compiler.Parser;

import compiler.Lexer.Symbol;

import java.util.ArrayList;

abstract class Expression extends Statement{
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
    public void printNode() {
        System.out.printf("\t rhs: %s%s%s", lhs.getRep(), operator, rhs.getRep());
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }
}
class LogicalAnd extends LogicalExpression{
    public LogicalAnd(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "&&");
    }

    @Override
    public void printNode() {

    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"AND op:\n");
        super.prettyPrint(indentation+" ");

    }
}
class LogicalOr extends LogicalExpression{
    public LogicalOr(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "||");
    }

    @Override
    public void printNode() {

    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"OR op:\n");
        super.prettyPrint(indentation+" ");

    }
}
/*--------------------------------------------------------------------------------------------------------------------*/
/** EQUALITY EXPRESSION:
 *             equalityExpression -> comparisonExpression ( ( NOT_EQUAL | EQUAL ) comparisonExpression  )*
 * */
abstract class EqualityExpression extends Expression{

    public EqualityExpression(int line, Expression lhs, Expression rhs, String operator) {
        super( line,  lhs,  rhs,  operator);
    }
    @Override
    public void printNode() {
        System.out.printf(" \t rhs: %s%s%s", lhs.getRep(), operator, rhs.getRep());
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }
    @Override
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);

    }
}
class NotEqualComparison extends EqualityExpression{

    public NotEqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!=");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Not equal comp:\n");
        super.prettyPrint(indentation+" ");

    }
}
class EqualComparison extends EqualityExpression{

    public EqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "==");
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Equality comp:\n");
        super.prettyPrint(indentation+" ");
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
    public void printNode() {
        System.out.printf("\t rhs: %s%s%s", lhs.getRep(), operator, rhs.getRep());
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);

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
    public void printNode() {
        System.out.printf("\t rhs: %s%s%s", lhs.getRep(), operator, rhs.getRep());
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);

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
    public void printNode() {
        System.out.printf("\t rhs: %s%s%s", lhs.getRep(), operator, rhs.getRep());
    }

    @Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);

    }
}
class UnaryNegateOperation extends UnaryExpression{
    public UnaryNegateOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!");
    }

    @Override
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);
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
    public void printNode() { System.out.printf("\t rhs: %s%s%s", lhs.getRep(), operator, rhs.getRep());
    }@Override
    public String getRep() {
        return lhs.getRep() + operator + rhs.getRep();
    }

    @Override
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);

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
    public void prettyPrint(String indentation) {
        super.prettyPrint(indentation);

    }
}
class FunctionCallExpression extends PrimaryExpression{
    Symbol identifier;
    ArrayList<Expression> expressionParams;

    public FunctionCallExpression(int line, Symbol identifier,ArrayList<Expression> expressionParams ) {
        super(line);
        this.identifier= identifier;
        this.expressionParams = expressionParams;
    }

    @Override
    public void printNode() {
        System.out.printf("\tFunction call exp: %s(", identifier.image());
        String string = "";
        for(Expression e : expressionParams){
            string = string.concat(e.getRep());
            string = string.concat(" ");
        }
        string = string.concat(")\n");
        System.out.print(string);
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
}

class LiteralExpression extends PrimaryExpression{
    Symbol literal;
    public LiteralExpression(int line, Symbol literal) {
        super(line);
        this.literal = literal;
    }

    @Override
    public void printNode() {
        System.out.printf("\t rhs: %s",literal.image());
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
}
class ParanExpression extends PrimaryExpression{
    ArrayList<Expression> expressions;
    public ParanExpression(int line, ArrayList<Expression> expressions) {
        super(line);
        this.expressions = expressions;
    }

    @Override
    public void printNode() {
        String string = "(";
        for(Expression e : expressions){
            string = string.concat(e.getRep());
        }
        System.out.println(string + ")");
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
}

/*--------------------------------------------------------------------------------------------------------------------*/
class IdentifierExpression extends Expression{
    Symbol id;
    public IdentifierExpression(int line, Symbol id) {
        super(line);
        this.id = id;
    }

    @Override
    public void printNode() {
        System.out.printf("%s\n",id.image());
    }

    @Override
    public String getRep() {
        return id.image();
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
}
class IndexOp extends Expression {
    Symbol identifier;
    Expression index;

    public IndexOp(int line, Symbol identifier, Expression index) {
        super(line);
        this.identifier = identifier;
        this.index = index;
    }

    @Override
    public void printNode() {

    }

    @Override
    public String getRep() {
        return null;
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
}

class DotOperation extends Expression{

    public DotOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ".");
    }

    @Override
    public void printNode() {

    }

    @Override
    public String getRep() {
        return null;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Dot op:\n");
        super.prettyPrint(indentation+" ");

    }
}

class DotOperation2 extends Expression{
    Symbol field;
    Expression lhs;

    public DotOperation2(int line, Expression lhs, Symbol field) {
        super(line);
        this.field = field;
    }

    @Override
    public void printNode() {

    }

    @Override
    public String getRep() {
        return null;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"Dot op:\n");
        if(lhs != null)
            lhs.prettyPrint(indentation+" " );
        //super.prettyPrint(indentation+" ");
        System.out.printf(indentation+" %s",field);


    }
}

