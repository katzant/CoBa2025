public class TypeCodegen {
    private Type type;
    private int Index;
    private boolean local;

    public TypeCodegen(Type type, int index, boolean local) {
        this.type = type;
        Index = index;
        this.local = local;
    }

    public Type getType() {
        return type;
    }

    public int getIndex() {
        return Index;
    }

    public boolean isLocal() {
        return local;
    }

}
