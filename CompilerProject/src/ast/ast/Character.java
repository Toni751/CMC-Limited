package ast.ast;

import checker.Visitor;

public class Character extends Terminal {

    public Character(String spelling) {
        this.spelling = spelling;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitCharacter(this, arg);
    }
}
