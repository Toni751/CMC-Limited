package checker;

import ast.ast.Declaration;
import ast.ast.FunctionDeclaration;

import java.util.ArrayList;
import java.util.List;

public class IdentificationTable {
    public List<IdEntry> table = new ArrayList<>();
    private int level = 0;

    public IdentificationTable() {
    }

    public void enter(String id, Declaration attr) {
        IdEntry entry = find(id);
        if (entry != null && entry.level == level) {
            System.out.println(id + "declared twice");
            System.out.println("RULE 3: A name (variable or function) may only be defined once at each scope level.");
        } else {
            table.add(new IdEntryDeclaration(level, id, attr));
        }
    }

    public void enter(String id, FunctionDeclaration attr) {
        IdEntry entry = find(id);
        if (entry != null && entry.level == level) {
            System.out.println(id + "declared twice");
        } else {
            table.add(new IdEntryFunction(level, id, attr));
        }
    }

    public void updateVariableDeclaration(String id, Declaration newDeclaration) {
        IdEntry entry = find(id);
        if (entry != null && entry.level == level && entry instanceof IdEntryDeclaration) {
            ((IdEntryDeclaration) entry).attr = newDeclaration;
        } else {
            System.out.println("Cannot update variable declaration for " + id);
        }
    }

    public Object retrieve(String id) {
        IdEntry entry = find(id);
        if (entry != null) {
            if (entry instanceof IdEntryDeclaration) {
                return ((IdEntryDeclaration) entry).attr;
            } else {
                return ((IdEntryFunction) entry).attr;
            }
        } else {
            return null;
        }
    }

    public void openScope() {
        ++level;
    }

    public void closeScope() {
        int pos = table.size() - 1;
        while (pos >= 0 && table.get(pos).level == level) {
            table.remove(pos);
            pos--;
        }
        level--;
    }

    public int getScope() {
        return level;
    }

    public String getLastFunctionId() {
        for (int i = table.size() - 1; i >= 0; i--) {
            if (table.get(i) instanceof IdEntryFunction) {
                return table.get(i).id;
            }
        }
        return null;
    }

    private IdEntry find(String id) {
        for (int i = table.size() - 1; i >= 0; i--) {
            if (table.get(i).id.equals(id)) {
                return table.get(i);
            }
        }
        return null;
    }
}
