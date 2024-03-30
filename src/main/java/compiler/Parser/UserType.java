package compiler.Parser;

import java.util.ArrayList;

public class UserType extends GenericType {
    ArrayList<GenericType> fields;

    public UserType(Block block) {
        fields = new ArrayList<>();
        for(Statement s : block.statements){
            fields.add(s.getType());
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
