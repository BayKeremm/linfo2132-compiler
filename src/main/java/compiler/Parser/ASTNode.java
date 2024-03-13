package compiler.Parser;

abstract class ASTNode {
    public static Program program;

    protected int line;

    protected ASTNode(int line){
        this.line = line;
    }

    public int line(){return this.line;}

    public void printNode(){
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA");
    }

}
