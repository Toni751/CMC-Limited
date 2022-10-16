package ast.ast;

import checker.Visitor;

public class CharacterValue extends Value {
    public Character character;

    public CharacterValue(Character character) {
        this.character = character;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitCharacterValue(this, arg);
    }
}
