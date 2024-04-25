package compiler.semantics;

import compiler.Parser.Block;
import compiler.Parser.GenericType;
import compiler.Parser.Statement;

import java.util.ArrayList;
import java.util.HashMap;

public class UserType extends GenericType {
    ArrayList<GenericType> fields;
    HashMap<String, GenericType> members;
    public UserType(Block block) {
        members = new HashMap<>();
        fields = new ArrayList<>();
        for(Statement s : block.getStatements()){
            fields.add(s.getType());
            members.put(s.getVariableName(),s.getType());
        }
    }

    @Override
    public String toString() {
        return fields.toString();
    }

    @Override
    public String type() {
        return fields.toString();
    }
}
