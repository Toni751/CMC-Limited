package ast.ast;

import checker.Visitor;

public class ReadCharDeclaration extends Declaration {
    public Type type;

    public ReadCharDeclaration(Identifier identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitReadCharDeclaration(this, arg);
    }
}
