package compiler.Parser;

abstract class ASTNode {
    public static Program program;


    public ASTNode(){

    }

    public abstract boolean equals(Object o);
}
