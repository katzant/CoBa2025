import java.util.*;

public class InterferenceGraph {

    public Map<String, Set<String>> getGraph(GraphNode start) {
        Map<String, Set<String>> interferenceGraph = new HashMap<>();

        List<GraphNode> nodes = getNodes(start);

        for (GraphNode node : nodes) {
            for (String def : node.getDef()) {
                interferenceGraph.put(def, new HashSet<>());
            }
            for (String out : node.getOut()) {
                interferenceGraph.put(out, new HashSet<>());
            }
        }

        for (GraphNode node : nodes) {
            for (String def : node.getDef()) {
                for (String out : node.getOut()) {
                    if (!def.equals(out)) {
                        interferenceGraph.get(out).add(def);
                        interferenceGraph.get(def).add(out);
                    }
                }
            }
        }
        return interferenceGraph;
    }

    private List<GraphNode> getNodes(GraphNode start) {
        List<GraphNode> nodes = new ArrayList<>();
        Queue<GraphNode> queue = new LinkedList<>();
        Set<GraphNode> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();
            nodes.add(current);

            for (GraphNode node : current.getNext()) {
                if (!visited.contains(node)) {
                    visited.add(node);
                    queue.add(node);
                }
            }
        }

        return nodes;
    }
}
