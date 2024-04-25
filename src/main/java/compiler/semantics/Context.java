package compiler.semantics;

import compiler.Parser.GenericType;

import java.util.HashMap;

public class Context extends ContextGod {
    HashMap<String, GenericType> symbolTable;
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
