import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphNode {

    private Set<String> in;
    private Set<String> out;
    private Set<String> def;
    private Set<String> use;
    private List<GraphNode> next;

    public GraphNode() {
        this.in = new HashSet<>();
        this.out = new HashSet<>();
        this.def = new HashSet<>();
        this.use = new HashSet<>();
        this.next = new ArrayList<>();
    }

    public Set<String> getIn() {
        return in;
    }

    public Set<String> getOut() {
        return out;
    }

    public Set<String> getDef() {
        return def;
    }

    public Set<String> getUse() {
        return use;
    }

    public List<GraphNode> getNext() {
        return next;
    }

    public void addNext(GraphNode graphNode) {
        next.add(graphNode);
    }

}
