package compiler.semantics;

import compiler.Parser.GenericType;

import java.util.HashMap;

public abstract class ContextGod {
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
