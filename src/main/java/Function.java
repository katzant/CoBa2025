import java.util.List;

public class Function {
    private List<Type> parameters;
    private Type returnValue;

    public Function(List<Type> parameters, Type returnValue) {
        this.parameters = parameters;
        this.returnValue = returnValue;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    public void setParameters(List<Type> parameters) {
        this.parameters = parameters;
    }

    public Type getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Type returnValue) {
        this.returnValue = returnValue;
    }
}
