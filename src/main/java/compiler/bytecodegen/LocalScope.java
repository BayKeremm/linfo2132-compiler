package compiler.bytecodegen;

import compiler.semantics.GenericType;
import org.objectweb.asm.Label;

import java.util.HashMap;

import static java.lang.System.exit;

public class LocalScope {
    int currIndex = 0;
    HashMap<String, Integer> varIndexTable;
    HashMap<String, GenericType> varTypeTable;
    LocalScope prevScope;
    LocalScope nextScope;
    Label startLabel;
    Label endLabel;

    public LocalScope() {
        varIndexTable = new HashMap<>();
        varTypeTable = new HashMap<>();
        this.startLabel = new Label();
        this.endLabel = new Label();
    }

    public void setStartIndex(int index){
        this.currIndex = index;
    }
    public int getCurrIndex(){
        return this.currIndex;
    }

    public LocalScope getPrevScope() {
        return prevScope;
    }

    public void setPrevScope(LocalScope prevScope) {
        this.prevScope = prevScope;
    }

    public LocalScope getNextScope() {
        return nextScope;
    }

    public void setNextScope(LocalScope nextScope) {
        this.nextScope = nextScope;
    }

    public Label getStartLabel() {
        return startLabel;
    }

    public Label getEndLabel() {
        return endLabel;
    }

    public int getIndex(String name) {
        if(varIndexTable.containsKey(name)){
            return varIndexTable.get(name);
        }
        else{
            var prev = this.prevScope;
            while(prev != null){
                var ret = prev.getIndex(name);
                if(ret != -1){
                    return ret;
                }
                else{
                    prev = prev.prevScope;
                }
            }

        }
        //System.err.println("ERROR IN GET INDEX LOCAL SCOPE");
        //exit(1);
        return 0;
    }
    public GenericType getType(String name){
        if(varTypeTable.containsKey(name)){
            return varTypeTable.get(name);
        }
        else{
            var prev = this.prevScope;
            while(prev != null){
                var ret = prev.getType(name);
                if(ret != null){
                    return ret;
                }
                else{
                    prev = prev.prevScope;
                }
            }

        }
        System.err.println("ERROR IN GET TYPE LOCAL SCOPE");
        exit(1);
        return null;
    }


    public void addToTable(String name, GenericType type) {
        varIndexTable.put(name, currIndex);
        varTypeTable.put(name, type);
        currIndex++;
    }
}
