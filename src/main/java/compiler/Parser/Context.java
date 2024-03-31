package compiler.Parser;

import java.util.HashMap;

abstract class ContextGod {
    ContextGod prevContext;
    public ContextGod(ContextGod prev){
        this.prevContext = prev;
    }
    public abstract boolean containsId(String id);
    public abstract boolean addToContext(String id, GenericType t);
    public abstract GenericType getVarType(String id);
    public abstract void debugContext(String indentation);

}

public class Context extends ContextGod {
    HashMap<String,GenericType> symbolTable;
    public Context(ContextGod prev){
        super(prev);
        this.symbolTable = new HashMap<String, GenericType>();
    }

    public boolean containsId(String id){
        return symbolTable.containsKey(id);
    }

    @Override
    public boolean addToContext(String id, GenericType type) {
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
        System.out.println(indentation + "File Context start:");
        System.out.println(indentation + " - SymbolTable:");
        System.out.println(indentation +"  "+ symbolTable.toString());
        System.out.println(indentation + " - Previous Context");
        if(prevContext != null){
            prevContext.debugContext(indentation+"   ");
        }else{
            System.out.println(indentation+" - It is null;");
        }
    }
}
