package checker;

import ast.ast.Declaration;
import ast.ast.FunctionDeclaration;

public class IdEntryFunction extends IdEntry{
    public FunctionDeclaration attr;

    public IdEntryFunction(int level, String id, FunctionDeclaration attr) {
        this.level = level;
        this.id = id;
        this.attr = attr;
    }
}
