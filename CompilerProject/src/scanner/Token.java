package scanner;

import static scanner.TokenKind.*;

public class Token {
    public TokenKind kind;
    public String spelling;
    private static final TokenKind[] KEYWORDS = {DEFINE, DO, ELSE, WITH, BYE, IF, THEN, DONE, UNTIL, NUM, CHAR, NUM_ARR, CHAR_ARR, SHOW, READ_CHAR, READ_NUM, PARAMS};

    public Token(TokenKind kind, String spelling) {
        this.kind = kind;
        this.spelling = spelling;

        if (kind == IDENTIFIER) {
            for (TokenKind tk : KEYWORDS)
                if (spelling.equals(tk.getSpelling())) {
                    this.kind = tk;
                    break;
                }
        }
    }

//    public boolean isAssignOperator() {
//        if (kind == ASSIGNMENT)
//            return containsOperator(spelling, ASSIGNOPS);
//        else
//            return false;
//    }
//
//    public boolean isAddOperator() {
//        if (kind == NUM_OPERATOR)
//            return containsOperator(spelling, ADDOPS);
//        else
//            return false;
//    }
//
//    public boolean isMulOperator() {
//        if (kind == NUM_OPERATOR)
//            return containsOperator(spelling, MULOPS);
//        else
//            return false;
//    }
//
//
//    private boolean containsOperator(String spelling, String OPS[]) {
//        for (int i = 0; i < OPS.length; ++i)
//            if (spelling.equals(OPS[i]))
//                return true;
//
//        return false;
//    }
//    private static final String[] ASSIGNOPS =
//            {
//                    "=",
//            };
//
//    private static final String[] ADDOPS =
//            {
//                    "+",
//                    "-",
//            };
//
//    private static final String[] MULOPS =
//            {
//                    "*",
//                    "/",
//                    "%",
//            };
}
