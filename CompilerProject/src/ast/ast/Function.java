package ast.ast;

public class Function
{
    public Identifier identifier;
    public VariableList varList;
    public Block block;

    public Function(Identifier identifier, VariableList varList, Block block) {
        this.identifier = identifier;
        this.varList = varList;
        this.block = block;
    }
}
