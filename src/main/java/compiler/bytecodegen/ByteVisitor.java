package compiler.bytecodegen;

import compiler.Parser.*;
import compiler.Parser.expressions.*;
import compiler.Parser.statements.ConstantVariable;
import compiler.Parser.statements.Procedure;

import java.lang.management.MonitorInfo;

public interface ByteVisitor {
    void visitProgram(Program program);
    void visitProcedure(Procedure procedure);

    void visitFunctionCall(FunctionCallExpression functionCallExpression);

    void visitLiteral(LiteralExpression expression);

    void visitPlus(PlusOperation plus);
    void visitMinus(MinusOperation minus);

    void visitMul(MultiplyOperation mul);
    void visitAND(LogicalAnd and);

    void visitConstantVariable(ConstantVariable variable);

    void visitIdentifier(IdentifierExpression expression);

}
