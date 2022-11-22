package ast.ast;

import checker.Visitor;

public class ReadNumDeclaration extends InitializedDeclaration {
    public ReadNumDeclaration(Identifier identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitReadNumDeclaration(this, arg);
    }
}
