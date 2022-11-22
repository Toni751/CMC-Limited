package scanner;

import static scanner.TokenKind.*;

public class Scanner {
    private SourceFile source;

    private char currentChar;
    private StringBuffer currentSpelling = new StringBuffer();
    private boolean isQuoteOpen = false;

    public Scanner(SourceFile source) {
        this.source = source;
        currentChar = source.getSource();
    }

    private void takeIt() {
        currentSpelling.append(currentChar);
        currentChar = source.getSource();
    }

    private boolean isLetter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void scanSeparator() {
        switch (currentChar) {
            case '#':
                takeIt();
                while (currentChar != SourceFile.EOL && currentChar != SourceFile.EOT)
                    takeIt();

                if (currentChar == SourceFile.EOL)
                    takeIt();
                break;

            case ' ':
            case '\n':
            case '\r':
            case '\t':
                takeIt();
                break;
        }
    }

    private TokenKind scanToken() {
        if ((isLetter(currentChar) || isDigit(currentChar)) && isQuoteOpen) {
            takeIt();
            return CHARACTER;
        } else if (isLetter(currentChar)) {
            takeIt();
            while (isLetter(currentChar) || isDigit(currentChar)) {
                takeIt();
            }

            return IDENTIFIER;
        } else if (isDigit(currentChar)) {
            takeIt();
            while (isDigit(currentChar)) {
                takeIt();
            }

            return NUMBER;
        }

        switch (currentChar) {
            case '\'':
                takeIt();
                isQuoteOpen = !isQuoteOpen;
                return QUOTE;
            case '>':
            case '<':
                takeIt();
                if (currentChar == '=') {
                    takeIt();
                }
                return COMPARATOR;
            case '=':
                takeIt();
                if (currentChar == '=') {
                    takeIt();
                    return COMPARATOR;
                }
                return ASSIGNMENT;
            case '!':
                takeIt();
                if (currentChar != '=') {
                    return ERROR;
                }
                takeIt();
                return COMPARATOR;
            case '+':
            case '-':
            case '*':
            case '/':
            case '|':
                takeIt();
                return OPERATOR;
            case ':':
                takeIt();
                    return COLON;
            case ',':
                takeIt();
                return COMMA;
            case ';':
                takeIt();
                return SEMICOLON;
            case '(':
                takeIt();
                return LEFT_PARAN;
            case ')':
                takeIt();
                return RIGHT_PARAN;
            case '[':
                takeIt();
                return LEFT_SQUARE_PARAN;
            case ']':
                takeIt();
                return RIGHT_SQUARE_PARAN;
            case SourceFile.EOT:
                return EOT;
            default:
                takeIt();
                return ERROR;
        }
    }

    public Token scan() {
        while (currentChar == '#' || currentChar == '\n' ||
                currentChar == '\r' || currentChar == '\t' ||
                currentChar == ' ') {
            scanSeparator();
        }
        currentSpelling = new StringBuffer("");
        TokenKind kind = scanToken();

        return new Token(kind, new String(currentSpelling));
    }
}
