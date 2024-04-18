package compiler.Parser;

import java.util.HashMap;

abstract class ContextGod {
    ContextGod prev;
    ContextGod next;
    String contextName;
    static HashMap<String, GenericType> constants_and_globals = new HashMap<>();
    public ContextGod(){
        this.contextName = "None";
    }

    public HashMap<String, GenericType> getConstants_and_globals() {
        return constants_and_globals;
    }

    public void setConstants_and_globals(HashMap<String, GenericType> constants_and_globals) {
        ContextGod.constants_and_globals = constants_and_globals;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public ContextGod getPrev() {
        return prev;
    }

    public void setPrev(ContextGod prev) {
        this.prev = prev;
    }

    public ContextGod getNext() {
        return next;
    }

    public void setNext(ContextGod next) {
        this.next = next;
    }

    public abstract boolean containsId(String id);

    public abstract boolean addToContext(String id, GenericType t);
    public abstract GenericType getVarType(String id);
    public abstract  void debugContext(String indentation);
    public abstract HashMap<String, GenericType> getContextTable();

}

public class Context extends ContextGod {
    HashMap<String,GenericType> symbolTable;
    public Context(){
        this.symbolTable = new HashMap<String, GenericType>();
    }

    public boolean containsId(String id){
        if(symbolTable.containsKey(id)) {
            return symbolTable.containsKey(id);
        }
        ContextGod prev = this.getPrev();
        while(prev != null){
            if(prev.containsId(id)){
                return true;
            }
            prev = prev.getPrev();
        }
        return false;
    }

    @Override
    public boolean addToContext(String id, GenericType type) {
        if(symbolTable.containsKey(id)){
            return false;
        }else{
            symbolTable.put(id,type);
            return true;
        }
    }

    public GenericType getVarType(String id){
        if(symbolTable.containsKey(id)){
            return symbolTable.get(id);
        }
        ContextGod prev = this.getPrev();
        while(prev != null){
            if(prev.containsId(id)){
                return prev.getVarType(id);
            }
            prev = prev.getPrev();
        }
        return null;
    }
    public void debugContext(String indentation){
        System.out.println(indentation + "File Context start:");
        System.out.println(indentation + " - SymbolTable:");
        System.out.println(indentation +"  "+ symbolTable.toString());
        System.out.println(indentation+"Prev:"+this.getPrev());
        System.out.println(indentation+"Next:"+this.getNext());

    }

    @Override
    public HashMap<String, GenericType> getContextTable() {
        return this.symbolTable;
    }


}
