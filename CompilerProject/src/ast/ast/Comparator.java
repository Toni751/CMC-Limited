package ast.ast;

import checker.Visitor;

public class Comparator extends Terminal {

    public Comparator(String spelling) {
        this.spelling = spelling;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitComparator(this, arg);
    }
}
