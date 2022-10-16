package ast.ast;

import checker.Visitor;

import java.util.ArrayList;
import java.util.List;

public class ValueList extends AST {
    public List<Value> values;

    public ValueList() {
        values = new ArrayList<>();
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitValueList(this, arg);
    }
}
