package ast;

import ast.ast.*;
import ast.ast.Character;
import ast.ast.Number;
import scanner.Scanner;
import scanner.Token;
import scanner.TokenKind;

import java.util.ArrayList;
import java.util.List;

import static scanner.TokenKind.*;
import static scanner.TokenKind.RIGHT_PARAN;

public class ParserAST {
    private Scanner scan;
    private Token currentTerminal;
    private final List<TokenKind> statements = new ArrayList<>();
    private final List<TokenKind> types = new ArrayList<>();
    private final List<TokenKind> values = new ArrayList<>();
    private final List<TokenKind> readInput = new ArrayList<>();

    public ParserAST(Scanner scan) {
        this.scan = scan;
        currentTerminal = scan.scan();
        initializeStatements();
        initializeTypes();
        initializeValues();
        initializeReadInput();
    }

    private void initializeStatements() {
        statements.add(IF);
        statements.add(DO);
        statements.add(SHOW);
    }

    private void initializeTypes() {
        types.add(NUM);
        types.add(NUM_ARR);
        types.add(CHAR);
        types.add(CHAR_ARR);
    }

    private void initializeValues() {
        values.add(IDENTIFIER);
        values.add(NUMBER);
        values.add(QUOTE);
    }

    private void initializeReadInput() {
        readInput.add(READ_NUM);
        readInput.add(READ_CHAR);
    }

    public Program parseProgram() {
        List<FunctionDeclaration> functions = new ArrayList<>();
        Block block = new Block();
        while (currentTerminal.kind != EOT) {
            switch (currentTerminal.kind) {
                case DEFINE:
                    functions.add(parseFunction());
                    break;
                default:
                    block.blockItems.add(parseBlockItem());
            }
        }

        return new Program(block, functions);
    }

    private FunctionDeclaration parseFunction() {
        accept(DEFINE);
        Identifier identifier = parseIdentifier();
        accept(WITH);
        VariableList variableList = parseVarList();
        accept(COLON);
        Block block = new Block();
        while (currentTerminal.kind != BYE && currentTerminal.kind != EOT) {
            block.blockItems.add(parseBlockItem());
        }
        accept(BYE);
        accept(SEMICOLON);
        return new FunctionDeclaration(identifier, variableList, block);
    }

    private Identifier parseIdentifier() {
        if (currentTerminal.kind == IDENTIFIER) {
            Identifier res = new Identifier( currentTerminal.spelling );
            currentTerminal = scan.scan();
            return res;
        }

        return new Identifier( "???" );
    }

    private VariableList parseVarList() {
        VariableList variableList = new VariableList();
        if (currentTerminal.kind == COLON) {
            return variableList;
        }

        variableList.variables.add(parseVar());
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            variableList.variables.add(parseVar());
        }

        return variableList;
    }

    private Variable parseVar() {
        if (types.contains(currentTerminal.kind)) {
            Type type = parseType();
            Identifier identifier = parseIdentifier();
            return new Variable(type, identifier);
        }
        System.out.println("Expected num, char or array but found " + currentTerminal.kind);
        return null;
    }

    private Type parseType() {
        if (types.contains(currentTerminal.kind)) {
            Type type = new Type(currentTerminal.spelling);
            currentTerminal = scan.scan();
            return type;
        }

        return new Type("???");
    }

    private BlockItem parseBlockItem() {
        if (statements.contains(currentTerminal.kind)) {
            return parseStatement(null);
        } else if (types.contains(currentTerminal.kind)) {
            return parseDeclaration(null);
        } else if (currentTerminal.kind == IDENTIFIER) {
            Identifier identifier = parseIdentifier();
            if (currentTerminal.kind == ASSIGNMENT) {
                return parseDeclaration(identifier);
            } else if (currentTerminal.kind == LEFT_PARAN) {
                return parseStatement(identifier);
            } else {
                System.out.println("Expected '=' or '(' after an identifier but found " + currentTerminal.kind);
            }
        } else {
            System.out.println("Expected a valid block function but found " + currentTerminal.kind);
        }
        return null;
    }

    private Statement parseStatement(Identifier identifier) {
        switch (currentTerminal.kind) {
            case IF:
                return parseIf();
            case DO:
                return parseDo();
            case SHOW:
                return parseShow();
            case LEFT_PARAN:
                return parseFunctionCall(identifier);
            default:
                System.out.println("Expected a statement but found " + currentTerminal.kind);
                return null;
        }
    }

    private IfStatement parseIf() {
        Block thenBlock = new Block();
        Block elseBlock = new Block();
        accept(IF);
        Comparison comparison = parseComparison();
        accept(THEN);
        while (currentTerminal.kind != ELSE && currentTerminal.kind != DONE && currentTerminal.kind != EOT) {
            thenBlock.blockItems.add(parseBlockItem());
        }
        if (currentTerminal.kind == ELSE) {
            accept(ELSE);
            while (currentTerminal.kind != DONE && currentTerminal.kind != EOT) {
                elseBlock.blockItems.add(parseBlockItem());
            }
        }
        accept(DONE);
        accept(SEMICOLON);
        return new IfStatement(comparison, thenBlock, elseBlock);
    }

    private Comparison parseComparison() {
        Value value1 = parseValue();
        Comparator comparator = parseComparator();
        Value value2 = parseValue();
        return new Comparison(comparator, value1, value2);
    }

    private Comparator parseComparator() {
        Comparator comparator = new Comparator("???");
        if (currentTerminal.kind == COMPARATOR) {
            comparator = new Comparator(currentTerminal.spelling);
        }
        currentTerminal = scan.scan();
        return comparator;
    }


    private DoStatement parseDo() {
        Block block = new Block();
        accept(DO);
        while (currentTerminal.kind != UNTIL && currentTerminal.kind != EOT) {
            block.blockItems.add(parseBlockItem());
        }
        accept(UNTIL);
        Comparison comparison = parseComparison();
        accept(SEMICOLON);
        return new DoStatement(block, comparison);
    }

    private ShowStatement parseShow() {
        accept(SHOW);
        accept(LEFT_PARAN);
        Identifier identifier = parseIdentifier();
        accept(RIGHT_PARAN);
        accept(SEMICOLON);
        return new ShowStatement(identifier);
    }

    private FunctionCall parseFunctionCall(Identifier identifier) {
        ValueList valueList = new ValueList();
        accept(LEFT_PARAN);
        accept(PARAMS);
        accept(COLON);
        if (values.contains(currentTerminal.kind)) {
            valueList = parseValueList();
        }
        accept(RIGHT_PARAN);
        accept(SEMICOLON);
        return new FunctionCall(identifier, valueList);
    }

    private ValueList parseValueList() {
        ValueList valueList = new ValueList();
        valueList.values.add(parseValue());
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            valueList.values.add(parseValue());
        }
        return valueList;
    }

    private Value parseValue() {
        Value value = parseSingleTermValue();

        while (currentTerminal.kind == OPERATOR) {
            Operator operator = parseOperator();
            Value value2 = parseSingleTermValue();
            value = new Operation(operator, value, value2);
        }
        return value;
    }
    private Operator parseOperator() {
        if(currentTerminal.kind == OPERATOR) {
            Operator operator = new Operator(currentTerminal.spelling);
            currentTerminal = scan.scan();
            return operator;
        }
        return new Operator("???");
    }

    private Value parseSingleTermValue() {
        if (currentTerminal.kind == IDENTIFIER) {
            return new VarValue(parseIdentifier());
        } else if (currentTerminal.kind == NUMBER) {
            return parseNumber();
        } else if (currentTerminal.kind == QUOTE) {
            return parseCharacter();
        }
        currentTerminal = scan.scan();
        return null;
    }

    private NumberValue parseNumber() {
        Number number = new Number(currentTerminal.spelling);
        currentTerminal = scan.scan();
        return new NumberValue(number);
    }

    private CharacterValue parseCharacter() {
        accept(QUOTE);
        Character character = new Character(currentTerminal.spelling);
        currentTerminal = scan.scan();
        accept(QUOTE);
        return new CharacterValue(character);
    }

    private Declaration parseDeclaration(Identifier identifier) {
        Declaration declaration = null;
        if (types.contains(currentTerminal.kind)) {
            Type currentVariableType = parseType();
            Identifier identifier1 = parseIdentifier();
            if (currentTerminal.kind == SEMICOLON) {
                declaration = new VariableDeclaration(identifier1, currentVariableType);
                accept(SEMICOLON);
                return declaration;
            }
            accept(ASSIGNMENT);
            if (currentVariableType.spelling.equals(CHAR_ARR.getSpelling())) {
                accept(LEFT_SQUARE_PARAN);
                declaration = new VariableInitialization(currentVariableType, parseCharArrDeclaration(), identifier1);
            } else if (currentVariableType.spelling.equals(NUM_ARR.getSpelling())) {
                accept(LEFT_SQUARE_PARAN);
                declaration = new VariableInitialization(currentVariableType, parseNumArrDeclaration(), identifier1);
            } else if (values.contains(currentTerminal.kind)) {
                declaration = new VariableInitialization(currentVariableType, parseValue(), identifier1);
            } else {
                declaration = parseReadDeclaration(identifier1);
            }
        } else {
            accept(ASSIGNMENT);
            if (currentTerminal.kind == LEFT_SQUARE_PARAN) {
                accept(LEFT_SQUARE_PARAN);
                if (currentTerminal.kind == QUOTE) {
                    declaration = new VariableInitialization(parseCharArrDeclaration(), identifier);
                } else if (currentTerminal.kind == NUMBER) {
                    declaration = new VariableInitialization(parseNumArrDeclaration(), identifier);
                }
            } else if (values.contains(currentTerminal.kind)) {
                declaration = new VariableInitialization(parseValue(), identifier);
            } else {
                declaration = parseReadDeclaration(identifier);
            }
        }
        accept(SEMICOLON);
        return declaration;
    }

    private Declaration parseReadDeclaration(Identifier identifier1) {
        Declaration declaration = null;
        if (currentTerminal.kind == READ_CHAR) {
            declaration = new ReadCharDeclaration(identifier1);
        } else if (currentTerminal.kind == READ_NUM) {
            declaration = new ReadNumDeclaration(identifier1);
        }
        currentTerminal = scan.scan();
        accept(LEFT_PARAN);
        accept(RIGHT_PARAN);
        return declaration;
    }

    private ValueList parseNumArrDeclaration() {
        ValueList valueList = new ValueList();
        valueList.values.add(parseNumber());
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            valueList.values.add(parseNumber());
        }
        accept(RIGHT_SQUARE_PARAN);
        return valueList;
    }

    private ValueList parseCharArrDeclaration() {
        ValueList valueList = new ValueList();
        valueList.values.add(parseCharacter());
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            valueList.values.add(parseCharacter());
        }
        accept(RIGHT_SQUARE_PARAN);
        return valueList;
    }

    private void accept(TokenKind expected) {
        if (currentTerminal.kind == expected) {
            currentTerminal = scan.scan();
        } else {
            System.out.println("Expected token of kind " + expected + ", but found " + currentTerminal.kind);
        }
    }
}
