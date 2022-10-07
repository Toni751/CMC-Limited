package ast.ast;

public class VariableInitialization extends Declaration {
    public Type type;
    public Value value;
    public ValueList valueList;

    public VariableInitialization(Type type, Value value, Identifier identifier) {
        this.type = type;
        this.value = value;
        this.identifier = identifier;
    }

    public VariableInitialization(Type type, ValueList valueList, Identifier identifier) {
        this.type = type;
        this.valueList = valueList;
        this.identifier = identifier;
    }

    public VariableInitialization(Value value, Identifier identifier) {
        this.value = value;
        this.identifier = identifier;
    }

    public VariableInitialization(ValueList valueList, Identifier identifier) {
        this.valueList = valueList;
        this.identifier = identifier;
    }
}
