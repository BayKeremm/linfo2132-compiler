package compiler.Parser.expressions;

import compiler.Parser.GenericType;
import compiler.Parser.statements.TypeDeclaration;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.Type;
import compiler.semantics.TypeVisitor;

public class ArrayInitializer extends Expression {

    TypeDeclaration type;
    Expression size;
    public ArrayInitializer(int line, TypeDeclaration type, Expression size) {
        super(line);
        this.type = type;
        this.size = size;
    }

    public Expression getSizeExpression(){
        return this.size;
    }

    @Override
    public String getRep() {
        return null;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitArrayInitializer(this);
    }

    @Override
    public GenericType getType() {
        return new Type(type.getTypeSymbol().token().image(),type.getIsArray());
    }

    public Expression getSize() {
        return size;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.print(indentation+"- Array init:\n");

        System.out.print(indentation+"   - type:\n");
        type.prettyPrint(indentation+"      ");
        System.out.print(indentation+"   - size:\n");
        size.prettyPrint(indentation+"      ");

    }

    @Override
    public String toString() {
        return "ArrayInitializer{" +
                 type +
                ", " + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        ArrayInitializer a = (ArrayInitializer) o;

        if(!this.size.equals(a.size)) return false;
        else if(!this.type.equals(a.type)) return false;

        return true;
    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitArrayInit(this);

    }
}
