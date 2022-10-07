package ast.ast;

public class VariableDeclaration extends Declaration{
    public Type type;

    public VariableDeclaration(Identifier identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }
}
