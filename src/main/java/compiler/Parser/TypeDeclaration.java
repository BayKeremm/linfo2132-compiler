package compiler.Parser;

import compiler.Lexer.Symbol;
import compiler.bytecodegen.ByteVisitor;
import compiler.semantics.TypeVisitor;

public class TypeDeclaration extends Statement {
    Symbol type;

    public Symbol getTypeSymbol() {
        return type;
    }

    public Boolean getIsArray() {
        return isArray;
    }

    Boolean isArray;

    protected TypeDeclaration(int line, Symbol type, Boolean isArray) {
        super(line);
        this.type = type;
        this.isArray = isArray;
    }


    @Override
    public String toString() {
        return type.image() + (isArray ? "[]":"") ;
    }

    @Override
    public void prettyPrint(String indentation) {
        System.out.printf(indentation+"Type dec: %s",type.image());
        if(isArray){
            System.out.print("[]\n");
        }

    }

    @Override
    public boolean equals(Object o) {
        TypeDeclaration t = (TypeDeclaration) o;

        if(!this.type.equals(t.type)) return false;
        else if(!(this.isArray == t.isArray)) return false;

        return true;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {

    }

    @Override
    public void codeGen(ByteVisitor b) {

    }
}
