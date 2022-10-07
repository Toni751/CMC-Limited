package ast.ast;

import java.util.ArrayList;
import java.util.List;

public class ValueList extends AST {
    public List<Value> values;

    public ValueList() {
        values = new ArrayList<>();
    }
}
