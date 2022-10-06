package parser;

import scanner.Scanner;
import scanner.Token;
import scanner.TokenKind;

import java.util.ArrayList;
import java.util.List;

import static scanner.TokenKind.*;

public class Parser {
    private Scanner scan;
    private Token currentTerminal;
    private final List<TokenKind> statements = new ArrayList<>();
    private final List<TokenKind> types = new ArrayList<>();
    private final List<TokenKind> values = new ArrayList<>();
    private final List<TokenKind> readInput = new ArrayList<>();

    public Parser(Scanner scan) {
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
        values.add(CHARACTER);
    }

    private void initializeReadInput() {
        readInput.add(READ_NUM);
        readInput.add(READ_CHAR);
    }

    public void parseProgram() {
        while (currentTerminal.kind != EOT) {
            switch (currentTerminal.kind) {
                case DEFINE:
                    parseFunction();
                    break;
                default:
                    parseBlock();
            }
        }
    }

    private void parseFunction() {
        accept(DEFINE);
        accept(IDENTIFIER);
        accept(WITH);
        parseVarList();
        accept(COLON);
        parseBlock();
        accept(BYE);
        accept(SEMICOLON);
    }

    private void parseVarList() {
        if (currentTerminal.kind == COLON) {
            return;
        }
        parseVar();
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            parseVar();
        }
    }

    private void parseVar() {
        if (types.contains(currentTerminal.kind)) {
            accept(currentTerminal.kind);
            accept(IDENTIFIER);
        } else {
            System.out.println("Expected num, char or array but found " + currentTerminal.kind);
        }
    }

    private void parseBlock() {
        if (statements.contains(currentTerminal.kind)) {
            parseStatement();
        } else if (types.contains(currentTerminal.kind)) {
            parseDeclaration();
        } else if (currentTerminal.kind == IDENTIFIER) {
            accept(IDENTIFIER);
            if (currentTerminal.kind == ASSIGNMENT) {
                parseDeclaration();
            } else if (currentTerminal.kind == LEFT_PARAN) {
                parseStatement();
            } else {
                System.out.println("Expected '=' or '(' after an identifier but found " + currentTerminal.kind);
            }
        } else {
            System.out.println("Expected a valid block function but found " + currentTerminal.kind);
        }
    }

    private void parseStatement() {
        switch (currentTerminal.kind) {
            case IF:
                parseIf();
                break;
            case DO:
                parseDo();
                break;
            case SHOW:
                parseShow();
                break;
            case LEFT_PARAN:
                parseFunctionCall();
                break;
            default:
                System.out.println("Expected a statement but found " + currentTerminal.kind);
                break;
        }
    }

    private void parseIf() {
        accept(IF);
        parseComparison();
        accept(THEN);
        parseBlock();
        if (currentTerminal.kind == ELSE) {
            accept(ELSE);
            parseBlock();
        }
        accept(DONE);
        accept(SEMICOLON);
    }

    private void parseComparison() {
        if (values.contains(currentTerminal.kind)) {
            parseIdentifierComparison();
        } else {
            System.out.println("Expected num or char identifier, but found " + currentTerminal.kind);
        }
    }

    private void parseIdentifierComparison() {
        accept(values);

        if (currentTerminal.kind == OPERATOR) {
            accept(OPERATOR);
            accept(values);
        }

        accept(COMPARATOR);
        accept(values);
        handleOperationOrThen();
    }

    private void handleOperationOrThen() {
        if (currentTerminal.kind == THEN) {
            return;
        }

        accept(OPERATOR);
        accept(values);
    }

    private void parseDo() {
        accept(DO);
        parseBlock();
        accept(UNTIL);
        parseComparison();
        accept(SEMICOLON);
    }

    private void parseShow() {
        accept(SHOW);
        accept(LEFT_PARAN);
        accept(IDENTIFIER);
        accept(RIGHT_PARAN);
        accept(SEMICOLON);
    }

    private void parseFunctionCall() {
        accept(LEFT_PARAN);
        accept(PARAMS);
        accept(COLON);
        if (values.contains(currentTerminal.kind)) {
            parseValueList();
        }
        accept(RIGHT_PARAN);
        accept(SEMICOLON);
    }

    private void parseValueList() {
        accept(values);
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            accept(values);
        }
    }

    private void parseDeclaration() {
        if (types.contains(currentTerminal.kind)) {
            TokenKind currentVariableType = currentTerminal.kind;
            accept(types);
            accept(IDENTIFIER);
            if (currentTerminal.kind == SEMICOLON) {
                accept(SEMICOLON);
                return;
            }
            accept(ASSIGNMENT);
            if (currentVariableType == CHAR_ARR) {
                parseCharArrDeclaration();
            } else if (currentVariableType == NUM_ARR) {
                parseNumArrDeclaration();
            } else {
                parseAssignment();
            }
        } else {
            accept(ASSIGNMENT);
            parseAssignment();
        }
        accept(SEMICOLON);
    }

    private void parseNumArrDeclaration() {
        accept(LEFT_SQUARE_PARAN);
        accept(NUMBER);
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            accept(NUMBER);
        }
        accept(RIGHT_SQUARE_PARAN);
    }

    private void parseCharArrDeclaration() {
        accept(LEFT_SQUARE_PARAN);
        parseChar();
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            parseChar();
        }
        accept(RIGHT_SQUARE_PARAN);
    }

    private void parseChar() {
        accept(QUOTE);
        accept(CHARACTER);
        accept(QUOTE);
    }

    private void parseAssignment() {
        if (values.contains(currentTerminal.kind)) {
            accept(values);
            if (currentTerminal.kind != SEMICOLON) {
                accept(OPERATOR);
                accept(values);
            }
        } else {
            accept(readInput);
            accept(LEFT_PARAN);
            accept(RIGHT_PARAN);
        }
    }

    private void accept(TokenKind expected) {
        if (currentTerminal.kind == expected) {
            currentTerminal = scan.scan();
        } else {
            System.out.println("Expected token of kind " + expected + ", but found " + currentTerminal.kind);
        }
    }

    private void accept(List<TokenKind> expected) {
        if (expected.contains(currentTerminal.kind)) {
            currentTerminal = scan.scan();
        } else {
            System.out.println("Expected token of any kind of " + expected + ", but found " + currentTerminal.kind);
        }
    }
}
