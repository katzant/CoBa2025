import java.util.HashMap;

public class ScopeCodegen {

    private HashMap<String, TypeCodegen> variables;
    private ScopeCodegen parent;
    private int lVIndex;

    public ScopeCodegen(ScopeCodegen parent) {
        this.parent = parent;
        variables = new HashMap<>();
    }

    public TypeCodegen get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.get(name);
        }
        return null;
    }

    public boolean addVariable(String name, Type type) {
        int index = 0;
        if (parent != null) {
            index = lVIndex;
            lVIndex++;
        }

        variables.put(name, new TypeCodegen(type, index, (parent != null)));
        return true;
    }

    public ScopeCodegen getParent() {
        return parent;
    }
}
