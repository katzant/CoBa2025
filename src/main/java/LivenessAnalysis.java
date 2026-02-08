
public class LivenessAnalysis extends PascalParserBaseVisitor<GraphNode> {

    @Override
    public GraphNode visitAssignment(PascalParser.AssignmentContext ctx) {
        GraphNode graphNode = visit(ctx.expression());
        graphNode.addDef(ctx.IDENTIFIER().getText());
        return graphNode;
    }
}
