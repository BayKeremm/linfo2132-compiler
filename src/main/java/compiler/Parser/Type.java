package compiler.Parser;

import java.util.ArrayList;
abstract class GenericType{
    public abstract String toString();
    public abstract String type();
}

public class Type extends GenericType{
    String type;
    Boolean isArray;
    Boolean isConstant;
    public Type(String type, Boolean isArray){
        this.type =type;
        this.isArray = isArray;
        this.isConstant= false;
    }

    @Override
    public String toString() {
        // DO NOT CHANGE
        return type+ (isArray ? "[]":"") ;
    }

    @Override
    public String type() {
        return type;
    }

    public void setIsConstant(Boolean bool){
        this.isConstant = bool;
    }
    public Boolean getIsConstant(Boolean bool){
        return this.isConstant ;
    }
}
