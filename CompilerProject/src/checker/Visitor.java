package checker;

import ast.ast.*;
import ast.ast.Character;
import ast.ast.Number;

public interface Visitor {
    Object visitProgram(Program p, Object arg);
    Object visitFunction(FunctionDeclaration f, Object arg);
    Object visitIdentifier(Identifier i, Object arg);
    Object visitVarList(VariableList v, Object arg);
    Object visitVariable(Variable v, Object arg);
    Object visitBlock(Block b, Object arg);
    Object visitIfStatement(IfStatement i, Object arg);
    Object visitComparison(Comparison c, Object arg);
    Object visitDoStatement(DoStatement d, Object arg);
    Object visitFunctionCall(FunctionCall f, Object arg);
    Object visitValueList(ValueList v, Object arg);
    Object visitShowStatement(ShowStatement s, Object arg);
    Object visitReadCharDeclaration(ReadCharDeclaration r, Object arg);
    Object visitReadNumDeclaration(ReadNumDeclaration r, Object arg);
    Object visitVariableDeclaration(VariableDeclaration v, Object arg);
    Object visitVariableInitialization(VariableInitialization v, Object arg);
    Object visitType(Type t, Object arg);
    Object visitOperation(Operation o, Object arg);
    Object visitOperator(Operator o, Object arg);
    Object visitComparator(Comparator c, Object arg);
    Object visitCharacter(Character c, Object arg);
    Object visitNumber(Number n, Object arg);
    Object visitNumberValue(NumberValue n, Object arg);
    Object visitCharacterValue(CharacterValue c, Object arg);
    Object visitVarValue(VarValue v, Object arg);
}
