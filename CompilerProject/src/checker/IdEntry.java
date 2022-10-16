package checker;

import ast.ast.Declaration;

public abstract class IdEntry {
    public int level;
    public String id;

    public String toString() {
        return level + "," + id;
    }
}
