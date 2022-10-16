package ast.ast;

import checker.Visitor;

public class FunctionDeclaration extends AST{
    public Identifier identifier;
    public VariableList varList;
    public Block block;

    public FunctionDeclaration(Identifier identifier, VariableList varList, Block block) {
        this.identifier = identifier;
        this.varList = varList;
        this.block = block;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitFunction(this, arg);
    }
}
