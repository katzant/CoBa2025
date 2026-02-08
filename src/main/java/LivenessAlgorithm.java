import java.util.*;

public class LivenessAlgorithm {

    public void calculate(GraphNode start) {
        List<GraphNode> allNodes = getNodes(start);

        boolean isChanged;
        do {
            isChanged = false;

            for (GraphNode n : allNodes) {
                Set<String> in = new HashSet<>(n.getIn());
                Set<String> out = new HashSet<>(n.getOut());

                n.getOut().clear();
                for (GraphNode succ : n.getNext()) {
                    n.getOut().addAll(succ.getIn());
                }

                Set<String> outMinusDef = new HashSet<>(n.getOut());
                outMinusDef.removeAll(n.getDef());

                n.getIn().clear();
                n.getIn().addAll(n.getUse());
                n.getIn().addAll(outMinusDef);

                if (!n.getIn().equals(in) || !n.getOut().equals(out)) {
                    isChanged = true;
                }
            }
        } while (isChanged);
    }

    private List<GraphNode> getNodes(GraphNode start) {
        List<GraphNode> nodes = new ArrayList<>();
        Queue<GraphNode> queue = new LinkedList<>();
        Set<GraphNode> done = new HashSet<>();

        queue.add(start);
        done.add(start);

        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();
            nodes.add(current);

            for (GraphNode node : current.getNext()) {
                if (!done.contains(node)) {
                    done.add(node);
                    queue.add(node);
                }
            }
        }

        return nodes;
    }
}