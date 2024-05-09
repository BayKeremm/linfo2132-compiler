package compiler.Parser;

public  abstract class GenericType{
    public abstract String toString();
    public abstract String type();
    public boolean isArray(){
        return false;
    }

}
