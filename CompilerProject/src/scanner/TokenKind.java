package scanner;

public enum TokenKind {
    IDENTIFIER,
    NUMBER,
    CHARACTER,
    ASSIGNMENT,
    OPERATOR,
    COMPARATOR,

    DEFINE("define"),
    DO("do"),
    ELSE("else"),
    WITH("with"),
    BYE("bye"),
    IF("if"),
    THEN("then"),
    DONE("done"),
    UNTIL("until"),
    NUM("num"),
    CHAR("char"),
    NUM_ARR("numArr"),
    CHAR_ARR("charArr"),
    SHOW("show"),
    READ_CHAR("readChar"),
    READ_NUM("readNum"),
    PARAMS("params"),

    COMMA(","),
    COLON(":"),
    SEMICOLON(";"),
    LEFT_PARAN("("),
    RIGHT_PARAN(")"),
    LEFT_SQUARE_PARAN("["),
    RIGHT_SQUARE_PARAN("]"),
    QUOTE("'"),

    EOT,

    ERROR;


    private String spelling = null;


    private TokenKind() {
    }


    private TokenKind(String spelling) {
        this.spelling = spelling;
    }


    public String getSpelling() {
        return spelling;
    }
}
