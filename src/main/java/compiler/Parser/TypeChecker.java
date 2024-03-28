package compiler.Parser;

public class TypeChecker {
    Program program;
    public TypeChecker(Program program){
        this.program = program;
    }

    public void typeCheck(){
        var constants = program.getConstantVariables();
        TypeVisitor v = new TypeVisitor();
        for(ConstantVariable e : constants){
            e.analyze(v);

        }
    }
}
