package checker;

import ast.ast.*;
import ast.ast.Character;
import ast.ast.Number;
import scanner.TokenKind;

import java.util.ArrayList;
import java.util.List;

public class Checker implements Visitor {

    private IdentificationTable idTable = new IdentificationTable();

    public void check(Program p) {
        p.visit(this, null);
    }

    @Override
    public Object visitProgram(Program p, Object arg) {
        idTable.openScope();
        for (int i = 0; i < p.functions.size(); i++) {
            p.functions.get(i).visit(this, null);
        }
        p.block.visit(this, null);
        idTable.closeScope();
        return null;
    }

    @Override
    public Object visitFunction(FunctionDeclaration f, Object arg) {
        String id = (String) f.identifier.visit(this, null);
        idTable.enter(id, f);
        idTable.openScope();
        f.varList.visit(this, null);
        f.block.visit(this, null);
        idTable.closeScope();
        return null;
    }

    @Override
    public Object visitIdentifier(Identifier i, Object arg) {
        return i.spelling;
    }

    @Override
    public Object visitVarList(VariableList v, Object arg) {
        for (VariableDeclaration var : v.variables) {
            var.visit(this, null);
        }
        return null;
    }

    @Override
    public Object visitBlock(Block b, Object arg) {
        for (BlockItem item : b.blockItems) {
            item.visit(this, null);
        }
        return null;
    }

    @Override
    public Object visitIfStatement(IfStatement i, Object arg) {
        i.cmpr.visit(this, null);
        i.thenBlock.visit(this, null);
        i.elseBlock.visit(this, null);
        return null;
    }

    @Override
    public Object visitComparison(Comparison c, Object arg) {
        Object operand1 = c.operand1.visit(this, null);
        Object operand2 = c.operand2.visit(this, null);
        if ((isOperandInt(operand1) && isOperandInt(operand2)) || (isOperandChar(operand1) && isOperandChar(operand2))) {
            c.comparator.visit(this, null);
        } else {
            System.out.println("RULE 14: Operands must be of the same simple type");
        }
        return null;
    }

    private boolean isOperandInt(Object operand) {
        boolean isInt = false;
        String numSpelling = TokenKind.NUM.getSpelling();
        if (operand instanceof VariableInitialization) {
            Type type = ((VariableInitialization) operand).type != null ? ((VariableInitialization) operand).type : ((VariableInitialization) operand).vd.type;
            isInt = numSpelling.equals(type.spelling);
        } else if (operand instanceof VariableDeclaration) {
            Type type = ((VariableDeclaration) operand).type;
            isInt = numSpelling.equals(type.spelling);
        }

        return operand instanceof Integer || operand instanceof ReadNumDeclaration || isInt || numSpelling.equals(operand);
    }

    private boolean isOperandChar(Object operand) {
        boolean isChar = false;
        String charSpelling = TokenKind.CHAR.getSpelling();
        if (operand instanceof VariableInitialization) {
            Type type = ((VariableInitialization) operand).type != null ? ((VariableInitialization) operand).type : ((VariableInitialization) operand).vd.type;
            isChar = charSpelling.equals(type.spelling);
        }

        return operand instanceof java.lang.Character || operand instanceof ReadCharDeclaration || isChar || charSpelling.equals(operand);
    }

    @Override
    public Object visitDoStatement(DoStatement d, Object arg) {
        d.block.visit(this, null);
        d.comparison.visit(this, null);
        return null;
    }

    @Override
    public Object visitFunctionCall(FunctionCall f, Object arg) {
        String id = (String) f.identifier.visit(this, null);
        Object retrieve = idTable.retrieve(id);
        if (retrieve == null) {
            System.out.println("RULE 5: A function must be defined before or after it is used.");
            return null;
        } else if (!(retrieve instanceof FunctionDeclaration)) {
            System.out.println("RULE 6: A variable name cannot be used as the function name in a function call.");
            return null;
        }

        FunctionDeclaration fd = (FunctionDeclaration) retrieve;
        f.declaration = fd;
        List<Object> values = (List<Object>) f.valueList.visit(this, null);
        List<VariableDeclaration> variables = fd.varList.variables;
        if (variables.size() != values.size()) {
            System.out.println("RULE 10: The number of arguments in a function call must match the number of parameters given in a function definition");
        }
        for (int i = 0; i < variables.size(); i++) {
            String variableSpelling = variables.get(i).type.spelling;
            String valueSpelling = findValueSpelling(values.get(i));
            if (!variableSpelling.equals(valueSpelling)) {
                System.out.println("RULE 20: The argument types given in a function call must match the types of the parameters given in the function declaration.");
            }
        }
        return null;
    }

    private String findValueSpelling(Object value) {
        if (value == null) {
            return null;
        }
        if (isOperandInt(value)) {
            return TokenKind.NUM.getSpelling();
        }
        if (isOperandChar(value)) {
            return TokenKind.CHAR.getSpelling();
        }
        VariableInitialization vi = (VariableInitialization) value;
        return vi.type.spelling;
    }

    @Override
    public Object visitValueList(ValueList v, Object arg) {
        List<Object> values = new ArrayList<>();
        for (Value value: v.values) {
            values.add(value.visit(this, null));
        }
        return values;
    }

    @Override
    public Object visitShowStatement(ShowStatement s, Object arg) {
        String id = (String) s.value.name.visit(this, null);
        if (getVarValueIdentifierDeclaration(id) != null) {
            return s.value.visit(this, null);
        } else {
            System.out.println("RULE 1: The parameter of the show function can only be an initialized variable");
        }
        return null;
    }

    @Override
    public Object visitReadCharDeclaration(ReadCharDeclaration r, Object arg) {
        String id = (String) r.identifier.visit(this, null);
        if (r.type != null) {
            idTable.enter(id, r);
            r.vd = new VariableDeclaration(r.identifier, r.type);
        } else {
            Object declaration = idTable.retrieve(id);
            if (declaration == null || declaration instanceof FunctionDeclaration || declaration instanceof ReadNumDeclaration) {
                System.out.println("RULE 18: Unable to initialize non-char variable with readChar.");
            } else if (declaration instanceof ReadCharDeclaration) {
                r.vd = ((ReadCharDeclaration) declaration).vd;
                idTable.updateVariableDeclaration(id, r);
            } else if (declaration instanceof VariableDeclaration) {
                r.vd = ((VariableDeclaration) declaration);
                validatePreviousDeclaration(TokenKind.CHAR, ((VariableDeclaration) declaration).type, id, r);
            } else if (declaration instanceof VariableInitialization) {
                r.vd = ((VariableInitialization) declaration).vd;
                validatePreviousDeclaration(TokenKind.CHAR, ((VariableInitialization) declaration).type, id, r);
            }
        }
        return null;
    }

    @Override
    public Object visitReadNumDeclaration(ReadNumDeclaration r, Object arg) {
        String id = (String) r.identifier.visit(this, null);
        if (r.type != null) {
            idTable.enter(id, r);
            r.vd = new VariableDeclaration(r.identifier, r.type);
        } else {
            Object declaration = idTable.retrieve(id);
            if (declaration == null || declaration instanceof FunctionDeclaration || declaration instanceof ReadCharDeclaration) {
                System.out.println("RULE 19: Unable to initialize non-number variable with readNum.");
            } else if (declaration instanceof ReadNumDeclaration) {
                r.vd = ((ReadNumDeclaration) declaration).vd;
                idTable.updateVariableDeclaration(id, r);
            } else if (declaration instanceof VariableDeclaration) {
                r.vd = ((VariableDeclaration) declaration);
                validatePreviousDeclaration(TokenKind.NUM, ((VariableDeclaration) declaration).type, id, r);
            } else if (declaration instanceof VariableInitialization) {
                r.vd = ((VariableInitialization) declaration).vd;
                validatePreviousDeclaration(TokenKind.NUM, ((VariableInitialization) declaration).type, id, r);
            }
        }
        return null;
    }

    private void validatePreviousDeclaration(TokenKind wantedType, Type declaration, String id, Declaration d) {
        boolean isWantedType = declaration != null && wantedType.getSpelling().equals(declaration.spelling);
        if (isWantedType) {
            idTable.updateVariableDeclaration(id, d);
        } else {
            if (wantedType == TokenKind.NUM) {
                System.out.println("RULE 19: A num variable can only be assigned a number.");
            } else {
                System.out.println("RULE 18: A char variable can only be assigned a character.");
            }
        }
    }

    @Override
    public Object visitVariableDeclaration(VariableDeclaration v, Object arg) {
        String id = (String) v.identifier.visit(this, null);
        idTable.enter(id, v);

        return null;
    }

    @Override
    public Object visitVariableInitialization(VariableInitialization v, Object arg) {
        String id = (String) v.identifier.visit(this, null);
        if (v.type != null) {
            String newValueType = findInitializationValueType(v);
            if (v.type.spelling.equals(newValueType)) {
                v.vd = new VariableDeclaration(v.identifier, v.type);
                idTable.enter(id, v);
            } else {
                System.out.println("The initial value type does not match with the new value type. Rules 16, 17, 18, 19.");
            }
        } else {
            Object declaration = idTable.retrieve(id);
            if (declaration == null) {
                System.out.println("RULE 4: A variable name must be defined before it is used.");
            } else if (declaration instanceof FunctionDeclaration) {
                System.out.println("RULE 7: A function name cannot be used as a variable.");
            } else if (declaration instanceof ReadNumDeclaration) {
                // v.type = ((ReadNumDeclaration) declaration).type;
                v.vd = ((ReadNumDeclaration) declaration).vd;
                validateNewVariableValue(TokenKind.NUM.getSpelling(), id, v);
            } else if (declaration instanceof ReadCharDeclaration) {
                // v.type = ((ReadCharDeclaration) declaration).type;
                v.vd = ((ReadCharDeclaration) declaration).vd;
                validateNewVariableValue(TokenKind.CHAR.getSpelling(), id, v);
            } else if (declaration instanceof VariableDeclaration) {
                // v.type = ((VariableDeclaration) declaration).type;
                v.vd = ((VariableDeclaration) declaration);
                validateNewVariableValue(((VariableDeclaration) declaration).type.spelling, id, v);
            } else if (declaration instanceof VariableInitialization) {
                // v.type = ((VariableInitialization) declaration).type;
                v.vd = ((VariableInitialization) declaration).vd;
                validateNewVariableValueInitializedBefore(v, id, (VariableInitialization) declaration);
            }
        }
        return null;
    }

    private void validateNewVariableValue(String type, String id, VariableInitialization v) {
        String newValueType = findInitializationValueType(v);
        if (!type.equals(newValueType)) {
            System.out.println("Cannot assign to a variable of type: " + type + " a value of type: " + newValueType);
            return;
        }
        idTable.updateVariableDeclaration(id, v);
    }

    private void validateNewVariableValueInitializedBefore(VariableInitialization v, String id, VariableInitialization declaration) {
        String currentValueType = findInitializationValueType(declaration);
        String newValueType = findInitializationValueType(v);
        if (currentValueType != null && currentValueType.equals(newValueType)) {
            idTable.updateVariableDeclaration(id, v);
        } else if (currentValueType != null) {
            switch (currentValueType) {
                case "num":
                    System.out.println("RULE 19: A num variable can only be assigned a number.");
                case "char":
                    System.out.println("RULE 18: A char variable can only be assigned a character.");
                case "numArr":
                    System.out.println("RULE 16: All elements inside of a numArr must be of number values.");
                case "charArr":
                    System.out.println("RULE 17: All elements inside of a charArr must be of character values.");
            }
        }
    }

    private String findInitializationValueType(VariableInitialization v) {
        if (v.value != null) {
            Object valueDeclaration = v.value.visit(this, null);
            if (isOperandInt(valueDeclaration)) {
                return TokenKind.NUM.getSpelling();
            }
            if (isOperandChar(valueDeclaration)) {
                return TokenKind.CHAR.getSpelling();
            }
            // if we get here we must have a char merge
            return TokenKind.CHAR_ARR.getSpelling();
        } else if (v.valueList != null){
            List<Object> values = (List<Object>) v.valueList.visit(this, null);
            if (isOperandInt(values.get(0))) {
                return TokenKind.NUM_ARR.getSpelling();
            }
            if (isOperandChar(values.get(0))) {
                return TokenKind.CHAR_ARR.getSpelling();
            }
        }
        return null;
    }

    @Override
    public Object visitType(Type t, Object arg) {
        return t.spelling;
    }

    @Override
    public Object visitOperation(Operation o, Object arg) {
        Object operand1 = o.operand1.visit(this, null);
        Object operand2 = o.operand2.visit(this, null);
        String operator = (String) o.operator.visit(this, null);
        switch (operator) {
            case "+":
                if (isOperandInt(operand1) && isOperandInt(operand2)) {
                    return TokenKind.NUM.getSpelling();
                } else {
                    System.out.println("RULE 12: Addition can only be done on variables of the same simple type");
                }
                break;
            case "|":
                if (isOperandChar(operand1) && isOperandChar(operand2)) {
                    return TokenKind.CHAR_ARR.getSpelling();
                } else {
                    System.out.println("RULE 12: Addition can only be done on variables of the same simple type");
                }
                break;
            case "-":
            case "*":
            case "/":
                if (isOperandInt(operand1) && isOperandInt(operand2)) {
                    return TokenKind.NUM.getSpelling();
                } else {
                    System.out.println("RULE 13: -, *, / can only be used between 2 number operands");
                }
                break;
        }
        return null;
    }

    @Override
    public Object visitOperator(Operator o, Object arg) {
        return o.spelling;
    }

    @Override
    public Object visitComparator(Comparator c, Object arg) {
        return c.spelling;
    }

    @Override
    public Object visitCharacter(Character c, Object arg) {
        return c.spelling.charAt(0);
    }

    @Override
    public Object visitNumber(Number n, Object arg) {
        return Integer.valueOf(n.spelling);
    }

    @Override
    public Object visitNumberValue(NumberValue n, Object arg) {
        return n.number.visit(this, null);
    }

    @Override
    public Object visitCharacterValue(CharacterValue c, Object arg) {
        return c.character.visit(this, null);
    }

    @Override
    public Object visitVarValue(VarValue v, Object arg) {
        String id = (String) v.name.visit(this, null);
        Declaration declaration = (Declaration) getVarValueIdentifierDeclaration(id);
        v.declaration = declaration;
        return declaration;
    }

    private Object getVarValueIdentifierDeclaration(String id) {
        Object idEntryDeclaration = idTable.retrieve(id);
        if (idEntryDeclaration == null) {
            System.out.println("RULE 4: A variable must be defined before it is used in a Comparison, Show Statement or Operation");
        } else if (idEntryDeclaration instanceof FunctionDeclaration) {
            System.out.println("RULE 14: A function cannot be used in a Comparison or Operation");
            System.out.println("RULE 7: A function cannot be used as a variable.");
        } else if (idEntryDeclaration instanceof VariableDeclaration && idTable.getScope() == 0) {
            System.out.println("RULE 15: A variable must be initialized before it is used in a Comparison or Operation");
        } else {
            return idEntryDeclaration;
        }
        return null;
    }
}
