package compiler.semantics;


import compiler.Parser.GenericType;

public class Type extends GenericType {
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
    public boolean isArray() {
        return isArray;
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
