package compiler.bytecodegen;

import compiler.Parser.*;
import compiler.Parser.expressions.*;
import compiler.Parser.statements.ConstantVariable;
import compiler.Parser.statements.Procedure;
import compiler.Parser.statements.ScopeVariable;
import compiler.Parser.statements.Variable;

import java.lang.management.MonitorInfo;

public interface ByteVisitor {
    void visitProgram(Program program);
    void visitProcedure(Procedure procedure);
    void visitConstantVariable(ConstantVariable variable);

    void visitVariable(Variable var);

    void visitScopeVariable(ScopeVariable variable);

    void visitIdentifier(IdentifierExpression expression);

    void visitArrayInit(ArrayInitializer init);

    void visitIndexOp(IndexOp op);
    void visitFunctionCall(FunctionCallExpression functionCallExpression);

    void visitLiteral(LiteralExpression expression);

    void visitPlus(PlusOperation plus);
    void visitMinus(MinusOperation minus);

    void visitModulo(ModuloOperation modulo);

    void visitUNegate(UnaryNegateOperation uNegate);

    void visitUMinus(UnaryMinusOperation uMinus);

    void visitMul(MultiplyOperation mul);

    void visitDivide(DivideOperation div);
    void visitAND(LogicalAnd and);
    void visitOR(LogicalOr or);

    void visitEquality(EqualComparison comp);
    void visitNotEqual(NotEqualComparison comp);
    void visitGE(GEComparison ge);
    void visitGT(GTComparison gt);

    void visitLE(LEComparison le);
    void visitLT(LTComparison lt);


}
