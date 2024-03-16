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
}
class NotEqualComparison extends EqualityExpression{

    public NotEqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!=");
    }

    @Override
    public void prettyPrint(String indentation) {

    }
}
class EqualComparison extends EqualityExpression{

    public EqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "==");
    }

    @Override
    public void prettyPrint(String indentation) {

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
}
class LTComparison extends ComparisionExpression{
    public LTComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "<");
    }

    @Override
    public void prettyPrint(String indentation) {

    }
}
class GTComparison extends ComparisionExpression{
    public GTComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ">");
    }

    @Override
    public void prettyPrint(String indentation) {

    }
}
class LEComparison extends ComparisionExpression{
    public LEComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "<=");
    }

    @Override
    public void prettyPrint(String indentation) {

    }
}
class GEComparison extends ComparisionExpression{
    public GEComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ">=");
    }

    @Override
    public void prettyPrint(String indentation) {

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

}
class MinusOperation extends TermExpression{
    public MinusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "-");
    }

    @Override
    public void prettyPrint(String indentation) {

    }
}
class PlusOperation extends TermExpression{
    public PlusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "+");
    }

    @Override
    public void prettyPrint(String indentation) {

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
}
class UnaryNegateOperation extends UnaryExpression{
    public UnaryNegateOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!");
    }

    @Override
    public void prettyPrint(String indentation) {

    }
}
class UnaryMinusOperation extends UnaryExpression{
    public UnaryMinusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "-");
    }

    @Override
    public void prettyPrint(String indentation) {

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
}
class MultiplyOperation extends FactorExpression{
    public MultiplyOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "*");
    }

    @Override
    public void prettyPrint(String indentation) {

    }
}

class DivideOperation extends FactorExpression{
    public DivideOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "/");
    }

    @Override
    public void prettyPrint(String indentation) {

    }
}
class ModuloOperation extends FactorExpression{
    public ModuloOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "%");
    }

    @Override
    public void prettyPrint(String indentation) {

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

    }

    @Override
    public String toString() {
        return "IdentifierExp{" +
                 id.image() +
                '}';
    }
}

