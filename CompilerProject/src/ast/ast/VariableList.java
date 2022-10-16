package ast.ast;

import checker.Visitor;

import java.util.ArrayList;
import java.util.List;

public class VariableList extends AST{
    public List<Variable> variables;

    public VariableList() {
        variables = new ArrayList<>();
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitVarList(this, arg);
    }
}
