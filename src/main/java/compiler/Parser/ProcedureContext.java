package compiler.Parser;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcedureContext extends ContextGod {

    HashMap<String, GenericType> arguments;
    HashMap<String, GenericType> procedureBlock;


    public ProcedureContext( ArrayList<Expression> parameters) {
        this.arguments = new HashMap<>();
        this.procedureBlock = new HashMap<>();
        for(Expression e: parameters){
            this.arguments.put(e.getVariableName(),e.getType());
        }
    }

    public boolean containsArgument(String id){
        return arguments.containsKey(id);
    }

    @Override
    public boolean containsId(String id) {
        return procedureBlock.containsKey(id);
    }

    @Override
    public boolean addToContext(String id, GenericType type) {
        if(containsId(id)){
            return false;
        }else{
            procedureBlock.put(id,type);
            return true;
        }
    }

    @Override
    public GenericType getVarType(String id) {
        if(containsId(id)){
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
        System.out.println(indentation + " - Previous Context");
        if(this.getPrev() != null){
            this.getPrev().debugContext(indentation+"   ");
        }else{
            System.out.println(indentation+" - It is null;");
        }

    }
}


