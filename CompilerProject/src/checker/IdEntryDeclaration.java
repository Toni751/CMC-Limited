package checker;

import ast.ast.Declaration;

public class IdEntryDeclaration extends IdEntry{
    public Declaration attr;

    public IdEntryDeclaration(int level, String id, Declaration attr) {
        this.level = level;
        this.id = id;
        this.attr = attr;
    }
}
