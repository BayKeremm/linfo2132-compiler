package compiler.Parser;

import java.util.HashMap;

import static java.lang.System.exit;

abstract class ContextGod {
    ContextGod prev;
    ContextGod next;
    // TODO: Maybe not necessary but still do it
    HashMap<String,ContextGod> procedureContextMemory;
    public ContextGod(){
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
    public abstract void debugContext(String indentation);

}

public class Context extends ContextGod {
    HashMap<String,GenericType> symbolTable;
    public Context(){
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
        System.out.println(indentation + " - Previous Context");
        if(this.getPrev() != null){
            this.getPrev().debugContext(indentation+"   ");
        }else{
            System.out.println(indentation+" - It is null;");
        }
    }
}
