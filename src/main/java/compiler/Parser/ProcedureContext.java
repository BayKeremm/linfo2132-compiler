package compiler.Parser;

import java.util.HashMap;

public class ProcedureContext extends ContextGod {

    HashMap<String, GenericType> arguments;
    HashMap<String, GenericType> procedureBlock;

    public ProcedureContext(ContextGod prev, HashMap<String,
            GenericType> arguments ) {
        super(prev);
        this.arguments = arguments;
        this.procedureBlock = new HashMap<>();
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
            return procedureBlock.get(id);
        }else if(prevContext != null){
            return prevContext.getVarType(id);
        }else{
            return null;
        }
    }

    @Override
    public void debugContext(String indentation) {
        System.out.println(indentation + "File Context start:");
        System.out.println(indentation + " - Arguments:");
        System.out.println(indentation +"  "+ arguments.toString());
        System.out.println(indentation + " - procedureBlock:");
        System.out.println(indentation +"  "+ procedureBlock.toString());
        System.out.println(indentation + " - Previous Context");
        if(prevContext != null){
            prevContext.debugContext(indentation+"   ");
        }else{
            System.out.println(indentation+" - It is null;");
        }

    }
}


