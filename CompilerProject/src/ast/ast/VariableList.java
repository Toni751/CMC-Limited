package ast.ast;

import java.util.ArrayList;
import java.util.List;

public class VariableList extends AST{
    public List<Variable> variables;

    public VariableList() {
        variables = new ArrayList<>();
    }
}
