package compiler.bytecodegen;

import compiler.Parser.*;

public interface ByteVisitor {
    void visitProgram(Program program);
    void visitProcedure(Procedure procedure);

    void visitFunctionCall(FunctionCallExpression functionCallExpression);

    void visitLiteral(LiteralExpression expression);

    void visitPlus(PlusOperation plus);

    void visitConstantVariable(ConstantVariable variable);

    void visitIdentifier(IdentifierExpression expression);
}
