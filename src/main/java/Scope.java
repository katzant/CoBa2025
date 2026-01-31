import java.util.HashMap;

public class Scope {

    private HashMap<String, Type> variables;
    private HashMap<String, Function> functions;
    private Scope parent;

    public Scope(Scope parent) {
        this.parent = parent;
        variables = new HashMap<>();
        functions = new HashMap<>();
    }

    public Type get(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parent != null) {
            return parent.get(name);
        }
        return null;
    }

    public boolean addVariable(String name, Type type) {
        if (!variables.containsKey(name)) {
            variables.put(name, type);
            return true;
        } else return false;
    }

    public Function getFunction(String name) {
        if (functions.containsKey(name)) {
            return functions.get(name);
        } else if (parent != null) {
            return parent.getFunction(name);
        }
        return null;
    }

    public boolean addFunction(String name, Function function) {
        if (!functions.containsKey(name)) {
            functions.put(name, function);
            return true;
        } else return false;
    }

    public Scope getParent() {
        return parent;
    }
}
