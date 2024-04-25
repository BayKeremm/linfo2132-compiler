package compiler.semantics;

import compiler.Parser.GenericType;

import java.util.ArrayList;

public class ProcedureInfo {
    private String name;
    private GenericType returnType;
    private ArrayList<GenericType> parameters;
    public ProcedureInfo(String name, GenericType returnType, ArrayList<GenericType> parameters){
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GenericType getReturnType() {
        return returnType;
    }

    public void setReturnType(GenericType returnType) {
        this.returnType = returnType;
    }

    public ArrayList<GenericType> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<GenericType> parameters) {
        this.parameters = parameters;
    }
}
