package ast.ast;

import checker.Visitor;
import compiler.Address;

public class VariableDeclaration extends Declaration{
    public Address address;

    public VariableDeclaration(Identifier identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitVariableDeclaration(this, arg);
    }
}
