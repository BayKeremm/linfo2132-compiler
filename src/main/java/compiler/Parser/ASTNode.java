package compiler.Parser;

abstract class ASTNode {
    public static Program program;


    public ASTNode(){

    }

    public abstract void printNode();

    public abstract boolean equals(Object o);
}
