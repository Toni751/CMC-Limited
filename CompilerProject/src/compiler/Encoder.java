package compiler;

import ast.ast.*;
import ast.ast.Character;
import ast.ast.Number;
import checker.Visitor;
import scanner.TokenKind;
import tam.Instruction;
import tam.Machine;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class Encoder implements Visitor {
    private int nextAdr = Machine.CB;
    private int currentLevel = 0;

    private void emit(int op, int n, int r, int d) {
        if (n > 255) {
            System.out.println("Operand too long");
            n = 255;
        }

        Instruction instr = new Instruction();
        instr.op = op;
        instr.n = n;
        instr.r = r;
        instr.d = d;

        if (nextAdr >= Machine.PB) {
            System.out.println("Program too large");
        } else {
            Machine.code[nextAdr++] = instr;
        }
    }

    private void patch(int adr, int d) {
        Machine.code[adr].d = d;
    }

    private int displayRegister(int currentLevel, int entityLevel) {
        if (entityLevel == 0) {
            return Machine.SBr;
        } else if (currentLevel - entityLevel <= 6) {
            return Machine.LBr + currentLevel - entityLevel;
        } else {
            System.out.println("Accessing across too many levels");
            return Machine.L6r;
        }
    }

    public void saveTargetProgram(String fileName) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
            for (int i = Machine.CB; i < nextAdr; i++) {
                Machine.code[i].write(out);
            }
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Trouble writing " + fileName);
        }
    }

    public void encode(Program p) {
        p.visit(this, null);
    }

    @Override
    public Object visitProgram(Program p, Object arg) {
        currentLevel = 0;

        int before = nextAdr;
        emit(Machine.JUMPop, 0, Machine.CB, 0);
        for (int i = 0; i < p.functions.size(); i++) {
            p.functions.get(i).visit(this, new Address(currentLevel, 0));
        }
        patch(before, nextAdr);
        p.block.visit(this, new Address());

        emit(Machine.HALTop, 0, 0, 0);

        return null;
    }

    @Override
    public Object visitFunction(FunctionDeclaration f, Object arg) {
        f.address = new Address(currentLevel, nextAdr);
        ++currentLevel;

        Address adr = new Address((Address) arg);
        int size = (Integer) f.varList.visit(this, adr);
        f.varList.visit(this, new Address(adr, -size));

        f.block.visit(this, new Address(adr, Machine.linkDataSize));

        emit(Machine.RETURNop, 0, 0, size);
        currentLevel--;
        return arg;
    }

    @Override
    public Object visitIdentifier(Identifier i, Object arg) {
        return null;
    }

    @Override
    public Object visitVarList(VariableList v, Object arg) {
        int startDisplacement = ((Address) arg).displacement;

        for (VariableDeclaration var: v.variables) {
            arg = var.visit(this, arg);
        }

        return ((Address) arg).displacement - startDisplacement;
    }

    @Override
    public Object visitBlock(Block b, Object arg) {
        for (BlockItem item: b.blockItems) {
            Object newArg = item.visit(this, arg);
            arg = newArg != null ? newArg : arg;
        }

        return null;
    }

    @Override
    public Object visitIfStatement(IfStatement i, Object arg) {
        i.cmpr.visit(this, true);

        int jump1Adr = nextAdr;
        emit(Machine.JUMPIFop, 0, Machine.CBr, 0);

        i.thenBlock.visit(this, arg);

        int jump2Adr = nextAdr;
        emit(Machine.JUMPop, 0, Machine.CBr, 0);

        patch(jump1Adr, nextAdr);
        i.elseBlock.visit(this, arg);
        patch(jump2Adr, nextAdr);

        return null;
    }

    @Override
    public Object visitComparison(Comparison c, Object arg) {
        String comp = (String) c.comparator.visit(this, null);
        c.operand1.visit(this, arg);
        c.operand2.visit(this, arg);

        int displacement = 0;
        switch (comp){
            case ">":
                displacement = Machine.gtDisplacement;
                break;
            case ">=":
                displacement = Machine.geDisplacement;
                break;
            case "<":
                displacement = Machine.ltDisplacement;
                break;
            case "<=":
                displacement = Machine.leDisplacement;
                break;
            case "==":
                displacement = Machine.eqDisplacement;
                break;
            case "!=":
                displacement = Machine.neDisplacement;
                break;
        }

        emit(Machine.CALLop, 0, Machine.PBr, displacement);
        return null;
    }

    @Override
    public Object visitDoStatement(DoStatement d, Object arg) {
        int startAdr = nextAdr;

        d.block.visit(this, arg);
        d.comparison.visit(this, true);

        emit(Machine.JUMPIFop, 0, Machine.CBr, startAdr);
        return null;
    }

    @Override
    public Object visitFunctionCall(FunctionCall f, Object arg) {
        f.valueList.visit(this, arg);

        Address address = f.declaration.address;
        int register = displayRegister(currentLevel, address.level);

        emit(Machine.CALLop, register, Machine.CB, address.displacement);

        return null;
    }

    @Override
    public Object visitValueList(ValueList v, Object arg) {
        for (Value value: v.values) {
            value.visit(this, arg);
        }
        return null;
    }

    @Override
    public Object visitShowStatement(ShowStatement s, Object arg) {
        s.value.visit(this, arg);

        String spelling = getVarValueDeclarationType(s.value);
        if (TokenKind.NUM.getSpelling().equals(spelling)) {
            emit(Machine.CALLop, 0, Machine.PBr, Machine.putintDisplacement);
        } else if (TokenKind.CHAR.getSpelling().equals(spelling)) {
            emit(Machine.CALLop, 0, Machine.PBr, Machine.putDisplacement);
        } else if (TokenKind.NUM_ARR.getSpelling().equals(spelling)) {
            int size = ((VariableInitialization) s.value.declaration).valueList.values.size();
            emit(Machine.CALLop, size, Machine.PBr, Machine.putIntArrayDisplacement);
        } else if (TokenKind.CHAR_ARR.getSpelling().equals(spelling)) {
            ValueList valueList = ((VariableInitialization) s.value.declaration).valueList;
            int size = valueList != null ? valueList.values.size() : 2; // the 2 comes from the merge operation
            emit(Machine.CALLop, size, Machine.PBr, Machine.putCharArrayDisplacement);
        }
        emit(Machine.CALLop, 0, Machine.PBr, Machine.puteolDisplacement);

        return null;
    }

    private String getVarValueDeclarationType(VarValue value) {
        return value.declaration.type != null ? value.declaration.type.spelling : ((InitializedDeclaration) value.declaration).vd.type.spelling;
    }

    @Override
    public Object visitReadCharDeclaration(ReadCharDeclaration r, Object arg) {
        if (r.type != null) {
            r.vd.visit(this, arg);
        }

        emit(Machine.PUSHop, 0, 0, 1);
        int register = displayRegister(currentLevel, r.vd.address.level);
        emit(Machine.CALLop, 0, Machine.PBr, Machine.getDisplacement);
        emit(Machine.STOREop, 1, register, r.vd.address.displacement);

        return new Address(r.vd.address, 1);
    }

    @Override
    public Object visitReadNumDeclaration(ReadNumDeclaration r, Object arg) {
        if (r.type != null) {
            r.vd.visit(this, arg);
        }

        emit(Machine.PUSHop, 0, 0, 1);
        int register = displayRegister(currentLevel, r.vd.address.level);
        emit(Machine.CALLop, 0, Machine.PBr, Machine.getintDisplacement);
        emit(Machine.STOREop, 1, register, r.vd.address.displacement);

        return new Address(r.vd.address, 1);
    }

    @Override
    public Object visitVariableDeclaration(VariableDeclaration v, Object arg) {
        v.address = (Address) arg;
        return new Address((Address) arg, 1);
    }

    @Override
    public Object visitVariableInitialization(VariableInitialization v, Object arg) {
        if (v.type != null) {
            v.vd.visit(this, arg);
        }

        if (v.valueList != null) {
            int arraySize = v.valueList.values.size();
            emit(Machine.PUSHop, 0, 0, arraySize);

            for (int i = arraySize - 1; i >= 0; i--) {
                v.valueList.values.get(i).visit(this, arg);
            }

            emit(Machine.STOREop, arraySize, displayRegister(currentLevel, v.vd.address.level), v.vd.address.displacement);
            return new Address(v.vd.address, arraySize);
        }

        emit(Machine.PUSHop, 0, 0, 1);
        v.value.visit(this, arg);

        int register = displayRegister(currentLevel, v.vd.address.level);
        emit(Machine.STOREop, 1, register, v.vd.address.displacement);
        // emit(Machine.LOADop, 1, register, v.vd.address.displacement);

        return new Address(v.vd.address, 1);
    }

    @Override
    public Object visitType(Type t, Object arg) {
        return t.spelling;
    }

    @Override
    public Object visitOperation(Operation o, Object arg) {
        String op = (String) o.operator.visit(this, null);
        o.operand1.visit(this, arg);
        o.operand2.visit(this, arg);

        int displacement = 0;
        switch (op){
            case "+":
                displacement = Machine.addDisplacement;
                break;
            case "-":
                displacement = Machine.subDisplacement;
                break;
            case "*":
                displacement = Machine.multDisplacement;
                break;
            case "/":
                displacement = Machine.divDisplacement;
                break;
            case "|":
                displacement = Machine.mergeDisplacement;
                break;
        }

        emit(Machine.CALLop, 0, Machine.PBr, displacement);
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
        int number = (int) n.number.visit(this, null);
        emit(Machine.LOADLop, 1, 0, number);
        return null;
    }

    @Override
    public Object visitCharacterValue(CharacterValue c, Object arg) {
        char character = (char) c.character.visit(this, null);
        emit(Machine.LOADLop, 1, 0, character);
        return null;
    }

    @Override
    public Object visitVarValue(VarValue v, Object arg) {
        Address address;
        if (v.declaration instanceof VariableDeclaration) {
            address = ((VariableDeclaration) v.declaration).address;
        } else {
            address = ((InitializedDeclaration) v.declaration).vd.address;
        }

        if (v.declaration instanceof VariableInitialization && ((VariableInitialization) v.declaration).valueList != null) {
            int size = ((VariableInitialization) v.declaration).valueList.values.size();
            int register = displayRegister(currentLevel, address.level);
            emit(Machine.LOADop, size, register, address.displacement);

            return null;
        }

        int register = displayRegister(currentLevel, address.level);
        emit(Machine.LOADop, 1, register, address.displacement);

        return null;
    }
}
