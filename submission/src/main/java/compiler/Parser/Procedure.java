package compiler.Parser;


import compiler.Lexer.Symbol;
import compiler.bytecodegen.ByteCodeGenerator;
import compiler.bytecodegen.ByteVisitor;

public class Procedure extends Statement implements ByteCodeGenerator {
    ProcedureDeclarator declarator;

    public ProcedureDeclarator getProcedureDeclarator() {
        return declarator;
    }

    public TypeDeclaration getProcedureReturnType() {
        return returnType;
    }

    public Symbol getProcedureIdentifier() {
        return identifier;
    }

    TypeDeclaration returnType;
    Symbol identifier;

    public Procedure(int line,ProcedureDeclarator declarator, TypeDeclaration returnType, Symbol identifier) {
        super(line);
        this.returnType = returnType;
        this.identifier = identifier;
        this.declarator = declarator;
    }


    @Override
    public void prettyPrint(String indentation) {
        System.out.println("Procedure: ");
        System.out.printf(indentation+"- return type: %s\n", returnType.toString());
        System.out.printf(indentation+"- name: %s\n", identifier.toString());
        declarator.prettyPrint(indentation);

    }

    @Override
    public boolean equals(Object o) {
        Procedure p = (Procedure) o;

        if(!this.returnType.equals(p.returnType)) return false;
        else if(!this.identifier.equals(p.identifier)) return false;
        else if(!this.declarator.equals(p.declarator)) return false;
        
        return true;
    }

    @Override
    public void typeAnalyse(TypeVisitor v) {
        v.visitProcedure(this);
    }

    @Override
    public void codeGen(ByteVisitor b) {
        b.visitProcedure(this);
    }
}

