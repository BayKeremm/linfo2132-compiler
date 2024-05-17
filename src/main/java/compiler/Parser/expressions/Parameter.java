package compiler.Parser.expressions;

import compiler.semantics.GenericType;
import compiler.Parser.statements.TypeDeclaration;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.Type;
import compiler.semantics.TypeVisitor;

public class Parameter extends Expression {
    TypeDeclaration type;
    Expression expression;
    public Parameter(int line, TypeDeclaration type, Expression expression) {
        super(line);
        this.type = type;
        this.expression = expression;
    }

    @Override
    public String getRep() {
        return type.toString() + " " +  expression.getRep();
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitParameter(this);
    }

    @Override
    public GenericType getType() {
        return new Type(type.getTypeSymbol().image(),type.getIsArray());
    }

    @Override
    public String getVariableName() {
        return expression.getRep();
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"- type:   %s\n",type);
        System.out.print(indentation+"- name:");
        expression.prettyPrint(indentation);

    }

    @Override
    public String toString() {
        return "Param{ " +
                type +
                ", " + expression +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        Parameter p = (Parameter) o;

        if(!this.type.equals(p.type)) return false;
        else if(!this.expression.equals(p.expression)) return false;
        
        return true;
    }

    @Override
    public void codeGen(ByteVisitor b) {

    }
}
