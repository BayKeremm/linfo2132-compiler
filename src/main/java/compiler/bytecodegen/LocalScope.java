package compiler.bytecodegen;

import compiler.Parser.GenericType;
import org.objectweb.asm.Label;

import java.util.HashMap;

public class LocalScope {
    int index = 0;
    HashMap<String, Integer> varIndexTable;
    HashMap<String, GenericType> varTypeTable;
    LocalScope prevScope;
    LocalScope nextScope;
    Label startLabel;
    Label endLabel;

    boolean startVisit;


    boolean endVisit;

    public LocalScope() {
        varIndexTable = new HashMap<>();
        varTypeTable = new HashMap<>();
        this.startLabel = new Label();
        this.endLabel = new Label();
        startVisit = false;
        endVisit = false;
    }

    public boolean isStartVisit() {
        return startVisit;
    }

    public void setStartVisit(boolean startVisit) {
        this.startVisit = startVisit;
    }

    public boolean isEndVisit() {
        return endVisit;
    }

    public void setEndVisit(boolean endVisit) {
        this.endVisit = endVisit;
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
        return varIndexTable.get(name);
    }
    public GenericType getType(String name){
        return varTypeTable.get(name);
    }


    public void addToTable(String name, GenericType type) {
        varIndexTable.put(name, index);
        varTypeTable.put(name, type);
        index++;
    }
}
