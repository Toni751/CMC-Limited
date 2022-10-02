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

    public Parser(Scanner scan) {
        this.scan = scan;
        currentTerminal = scan.scan();
        initializeStatements();
        initializeTypes();
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
        switch (currentTerminal.kind) {
            case NUM_IDENTIFIER:
                parseNumIdentifierComparison();
                break;
            case CHAR_IDENTIFIER:
                parseCharIdentifierComparison();
                break;
            default:
                System.out.println("Expected num or char identifier, but found " + currentTerminal.kind);
        }
    }

    private void parseNumIdentifierComparison() {
        accept(NUM_IDENTIFIER);
        if (currentTerminal.kind == NUM_OPERATOR) {
            accept(NUM_OPERATOR);
            accept(NUM_IDENTIFIER);
            accept(COMPARATOR);
            accept(NUM_IDENTIFIER);
            handleNumOperationOrThen();
        } else if (currentTerminal.kind == COMPARATOR) {
            accept(COMPARATOR);
            accept(NUM_IDENTIFIER);
            handleNumOperationOrThen();
        } else {
            System.out.println("Expected comparison but found " + currentTerminal.kind);
        }
    }

    private void handleNumOperationOrThen() {
        if (currentTerminal.kind == THEN) {
            return;
        }
        if (currentTerminal.kind == NUM_OPERATOR) {
            accept(NUM_OPERATOR);
            accept(NUM_IDENTIFIER);
        } else {
            System.out.println("Expected then or num operator, but found " + currentTerminal.kind);
        }
    }

    private void parseCharIdentifierComparison() {
        accept(CHAR_IDENTIFIER);
        if (currentTerminal.kind == MERGE) {
            accept(MERGE);
            accept(CHAR_IDENTIFIER);
            accept(COMPARATOR);
            accept(CHAR_IDENTIFIER);
            handleCharOperationOrThen();
        } else if (currentTerminal.kind == COMPARATOR) {
            accept(COMPARATOR);
            accept(CHAR_IDENTIFIER);
            handleCharOperationOrThen();
        } else {
            System.out.println("Expected comparison but found " + currentTerminal.kind);
        }
    }

    private void handleCharOperationOrThen() {
        if (currentTerminal.kind == THEN) {
            return;
        }
        if (currentTerminal.kind == MERGE) {
            accept(MERGE);
            accept(CHAR_IDENTIFIER);
        } else {
            System.out.println("Expected then or num operator, but found " + currentTerminal.kind);
        }
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
        if (currentTerminal.kind == NUM_IDENTIFIER || currentTerminal.kind == CHAR_IDENTIFIER) {
            parseIdentifierList();
        }
        accept(RIGHT_PARAN);
        accept(SEMICOLON);
    }

    private void parseIdentifierList() {
        accept(currentTerminal.kind);
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            if (currentTerminal.kind == NUM_IDENTIFIER || currentTerminal.kind == CHAR_IDENTIFIER) {
                accept(currentTerminal.kind);
            } else {
                System.out.println("Expected identifier but found " + currentTerminal.kind);
            }
        }
    }

    private void parseDeclaration() {
        if (types.contains(currentTerminal.kind)) {
            if (currentTerminal.kind == CHAR_ARR) {
                parseCharArrDeclaration();
            } else if (currentTerminal.kind == NUM_ARR) {
                parseNumArrDeclaration();
            } else {
                accept(currentTerminal.kind);
                accept(IDENTIFIER);
                if (currentTerminal.kind == ASSIGNMENT) {
                    parseAssignment();
                }
            }
            accept(SEMICOLON);
        } else if (currentTerminal.kind == ASSIGNMENT) {
            parseAssignment();
            accept(SEMICOLON);
        } else {
            System.out.println("Expected declaration/assignment but found " + currentTerminal.kind);
        }
    }

    private void parseNumArrDeclaration() {
        parseArray();
        accept(NUM_IDENTIFIER);
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            accept(NUM_IDENTIFIER);
        }
        accept(RIGHT_SQUARE_PARAN);
    }

    private void parseCharArrDeclaration() {
        parseArray();
        parseChar();
        while (currentTerminal.kind == COMMA) {
            accept(COMMA);
            parseChar();
        }
        accept(RIGHT_SQUARE_PARAN);
    }

    private void parseArray() {
        accept(currentTerminal.kind);
        accept(IDENTIFIER);
        accept(ASSIGNMENT);
        accept(LEFT_SQUARE_PARAN);
    }

    private void parseChar() {
        accept(QUOTE);
        accept(CHAR_IDENTIFIER);
        accept(QUOTE);
    }

    private void parseAssignment() {
        accept(ASSIGNMENT);
        if (currentTerminal.kind == NUM_IDENTIFIER || currentTerminal.kind == CHAR_IDENTIFIER) {
            accept(currentTerminal.kind);
        } else if (currentTerminal.kind == READ_CHAR || currentTerminal.kind == READ_NUM) {
            accept(currentTerminal.kind);
            accept(LEFT_PARAN);
            accept(RIGHT_PARAN);
        } else {
            System.out.println("Expected an identifier or readChar/readNum but found " + currentTerminal.kind);
        }
    }

    private void accept(TokenKind expected) {
        if (currentTerminal.kind == expected) {
            currentTerminal = scan.scan();
        } else {
            System.out.println("Expected token of kind " + expected + ", but found " + currentTerminal.kind);
        }
    }
}
