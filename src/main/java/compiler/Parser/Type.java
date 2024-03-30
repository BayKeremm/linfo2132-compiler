package compiler.Parser;

abstract class GenericType{
    public abstract String toString();
    public abstract String type();
}
public class Type extends GenericType{
    String type;
    Boolean isArray;
    public Type(String type, Boolean isArray){
        this.type =type;
        this.isArray = isArray;
    }

    @Override
    public String toString() {
        return type+ (isArray ? "[]":"");
    }

    @Override
    public String type() {
        return type;
    }
}


