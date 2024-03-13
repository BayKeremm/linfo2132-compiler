package compiler.Parser;

abstract class Expression extends ASTNode{
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

    public void printExpression(){
        System.out.println("Hello from expression:");
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
}
class LogicalAnd extends LogicalExpression{
    public LogicalAnd(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "&&");
    }
}
class LogicalOr extends LogicalExpression{
    public LogicalOr(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "||");
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
}
class NotEqualComparison extends EqualityExpression{

    public NotEqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!=");
    }
}
class EqualComparison extends EqualityExpression{

    public EqualComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "==");
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
}
class LTComparison extends ComparisionExpression{
    public LTComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "<");
    }
}
class GTComparison extends ComparisionExpression{
    public GTComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ">");
    }
}
class LEComparison extends ComparisionExpression{
    public LEComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "<=");
    }
}
class GEComparison extends ComparisionExpression{
    public GEComparison(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, ">=");
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
}
class MinusOperation extends TermExpression{
    public MinusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "-");
    }
}
class PlusOperation extends TermExpression{
    public PlusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "+");
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
}
class UnaryNegateOperation extends UnaryExpression{
    public UnaryNegateOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "!");
    }
}
class UnaryMinusOperation extends UnaryExpression{
    public UnaryMinusOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "-");
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
}
class MultiplyOperation extends FactorExpression{
    public MultiplyOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "*");
    }
}

class DivideOperation extends FactorExpression{
    public DivideOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "/");
    }
}
class ModuloOperation extends FactorExpression{
    public ModuloOperation(int line, Expression lhs, Expression rhs) {
        super(line, lhs, rhs, "%");
    }
}
/*--------------------------------------------------------------------------------------------------------------------*/




