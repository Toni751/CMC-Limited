package ast.ast;

import checker.Visitor;

public class Comparison extends AST {
    public Comparator comparator;
    public Value operand1;
    public Value operand2;

    public Comparison(Comparator comparator, Value operand1, Value operand2) {
        this.comparator = comparator;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    @Override
    public Object visit(Visitor v, Object arg) {
        return v.visitComparison(this, arg);
    }
}
