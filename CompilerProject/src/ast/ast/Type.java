package ast.ast;

import checker.Visitor;

public class Type extends Terminal{

    public Type(String spelling) {
        this.spelling = spelling;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitType(this, arg);
    }
}
