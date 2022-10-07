package ast.ast;

public class FunctionDeclaration extends AST{
    public Identifier identifier;
    public VariableList varList;
    public Block block;

    public FunctionDeclaration(Identifier identifier, VariableList varList, Block block) {
        this.identifier = identifier;
        this.varList = varList;
        this.block = block;
    }
}
