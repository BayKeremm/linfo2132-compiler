package compiler.Parser;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcedureContext extends ContextGod {

    HashMap<String, GenericType> arguments;
    HashMap<String, GenericType> procedureBlock;


    public ProcedureContext(ArrayList<Expression> parameters) {
        this.arguments = new HashMap<>();
        this.procedureBlock = new HashMap<>();
        for(Expression e: parameters){
            this.arguments.put(e.getVariableName(),e.getType());
        }
    }

    @Override
    public boolean containsId(String id) {
        if(procedureBlock.containsKey(id)){
            return true;
        }
        else if(this.arguments.containsKey(id)){
            return true;
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
        if(procedureBlock.containsKey(id)){
            return false;
        } else if (arguments.containsKey(id)) {
            return false;
        } else{
            procedureBlock.put(id,type);
            return true;
        }
    }

    @Override
    public GenericType getVarType(String id) {
        if(procedureBlock.containsKey(id)){
            return this.procedureBlock.get(id);
        }
        else if(this.arguments.containsKey(id)){
            return this.arguments.get(id);
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

    @Override
    public void debugContext(String indentation) {
        System.out.println(indentation + "Procedure Context start:");
        System.out.println(indentation + " - Arguments:");
        System.out.println(indentation +"  "+ arguments.toString());
        System.out.println(indentation + " - procedureBlock:");
        System.out.println(indentation +"  "+ procedureBlock.toString());
        System.out.println(indentation+"Prev:"+this.getPrev());
        System.out.println(indentation+"Next:"+this.getNext());
    }

    @Override
    public HashMap<String, GenericType> getContextTable() {
        this.arguments.putAll(this.procedureBlock);
        return arguments;
    }

}


