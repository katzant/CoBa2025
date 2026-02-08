public class LivenessAnalysis extends PascalParserBaseVisitor<GraphNode> {

    @Override
    public GraphNode visitAssignment(PascalParser.AssignmentContext ctx) {
        GraphNode graphNode = visit(ctx.expression());
        graphNode.addDef(ctx.IDENTIFIER().getText());
        return graphNode;
    }

    @Override
    public GraphNode visitAdditExpression(PascalParser.AdditExpressionContext ctx) {
        GraphNode node0 = visit(ctx.expression(0));
        GraphNode node1 = visit(ctx.expression(1));

        node0.getUse().addAll(node1.getUse());
        return node0;
    }

    @Override
    public GraphNode visitBoolExpression(PascalParser.BoolExpressionContext ctx) {
        GraphNode node0 = visit(ctx.expression(0));
        GraphNode node1 = visit(ctx.expression(1));

        node0.getUse().addAll(node1.getUse());
        return node0;
    }

    @Override
    public GraphNode visitMultExpression(PascalParser.MultExpressionContext ctx) {
        GraphNode node0 = visit(ctx.expression(0));
        GraphNode node1 = visit(ctx.expression(1));

        node0.getUse().addAll(node1.getUse());
        return node0;
    }

    @Override
    public GraphNode visitLiteralExpression(PascalParser.LiteralExpressionContext ctx) {
        GraphNode node = new GraphNode();
        if (ctx.literal().IDENTIFIER() != null) {
            node.getUse().add(ctx.literal().IDENTIFIER().getText());
        }
        return node;
    }

    @Override
    public GraphNode visitParenExpression(PascalParser.ParenExpressionContext ctx) {
        GraphNode node = new GraphNode();
        node.getUse().add(ctx.expression().getText());
        return node;
    }

    @Override
    public GraphNode visitUnaryExpression(PascalParser.UnaryExpressionContext ctx) {
        GraphNode node = new GraphNode();
        node.getUse().add(ctx.expression().getText());
        return node;
    }

}
