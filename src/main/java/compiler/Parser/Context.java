package compiler.Parser;

import java.util.HashMap;

public class Context {
    HashMap<String,GenericType> symbolTable;
    Context prevContext;
    public Context(Context prev){
        this.symbolTable = new HashMap<String, GenericType>();
        this.prevContext = prev;
    }

    public boolean containsId(String id){
        return symbolTable.containsKey(id);
    }

    public boolean addVariable(String id, GenericType type){
        if(containsId(id)){
            return false;
        }else{
            symbolTable.put(id,type);
            return true;
        }
    }


    public GenericType getVarType(String id){
        if(containsId(id)){
            return symbolTable.get(id);
        }else if(prevContext != null){
            return prevContext.getVarType(id);
        }else{
            return null;
        }
    }
    public void debugContext(String indentation){
        System.out.println(indentation + "Context start:");
        System.out.println(indentation + "SymbolTable:");
        System.out.println(indentation + symbolTable.toString());
        System.out.println(indentation + "Previous Context");
        if(prevContext != null){
            prevContext.debugContext(indentation+"   ");
        }
    }
}
