import org.antlr.v4.misc.Graph;

import java.util.*;

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
        return visit(ctx.expression());
    }

    @Override
    public GraphNode visitUnaryExpression(PascalParser.UnaryExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public GraphNode visitFuncExpression(PascalParser.FuncExpressionContext ctx) {
        return visit(ctx.function_call());
    }

    @Override
    public GraphNode visitFunction_call(PascalParser.Function_callContext ctx) {
        return new GraphNode();
    }

    @Override
    public GraphNode visitIf_statement(PascalParser.If_statementContext ctx) {
        GraphNode expressionNode = visit(ctx.expression());
        GraphNode thenNode = visit(ctx.statement(0));

        if (ctx.statement().size() != 1) {
            GraphNode elseNode = visit(ctx.statement(1));
            expressionNode.addNext(elseNode);
        }

        expressionNode.addNext(thenNode);
        return expressionNode;
    }

    @Override
    public GraphNode visitWhile_statement(PascalParser.While_statementContext ctx) {
        GraphNode expressionNode = visit(ctx.expression());

        GraphNode loopNode = null;

        if (ctx.statement() !=  null) {
            loopNode = visit(ctx.statement());
        } else {
            loopNode = visit(ctx.begin_end_block());
        }

        expressionNode.addNext(loopNode);

        List<GraphNode> loopEnds = new ArrayList<>();

        Queue<GraphNode> queue = new LinkedList<>();
        queue.add(loopNode);

        Set<GraphNode> done = new HashSet<>();
        done.add(loopNode);

        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();
            if (current.getNext().isEmpty()) {
                loopEnds.add(current);
            }
            for (GraphNode node : current.getNext()) {
                if (!done.contains(node)) {
                    done.add(node);
                    queue.add(node);
                }
            }
        }

        for (GraphNode end : loopEnds) {
            end.addNext(expressionNode);
        }

        expressionNode.addNext(new GraphNode());
        return expressionNode;
    }

    @Override
    public GraphNode visitBegin_end_block(PascalParser.Begin_end_blockContext ctx) {
        if (ctx.statements().isEmpty()) {
            return new GraphNode();
        }

        List<GraphNode> endsOfBlock = new ArrayList<>();

        GraphNode startOfBlock = null;

        for (int i = 0; i < ctx.statements().statement().size(); i++) {
            GraphNode next = visit(ctx.statements().statement(i));

            if (startOfBlock == null) {
                startOfBlock = next;
            }

            for (GraphNode end : endsOfBlock) {
                end.addNext(next);
            }

            endsOfBlock = new ArrayList<>();

            Queue<GraphNode> queue = new LinkedList<>();
            queue.add(next);

            Set<GraphNode> done = new HashSet<>();
            done.add(next);

            while (!queue.isEmpty()) {
                GraphNode current = queue.poll();
                if (current.getNext().isEmpty()) {
                    endsOfBlock.add(current);
                }
                for (GraphNode node : current.getNext()) {
                    if (!done.contains(node)) {
                        done.add(node);
                        queue.add(node);
                    }
                }
            }
        }
        return startOfBlock;
    }

    @Override
    public GraphNode visitStart(PascalParser.StartContext ctx) {
        if (ctx.statements().statement().isEmpty()) {
            return new GraphNode();
        }

        GraphNode startOfProgram = null;
        List<GraphNode> ends = new ArrayList<>();

        for (int i = 0; i < ctx.statements().statement().size(); i++) {
            GraphNode next = visit(ctx.statements().statement().get(i));

            if (startOfProgram == null) {
                startOfProgram = next;
            }

            for (GraphNode end : ends) {
                end.addNext(next);
            }

            ends = new ArrayList<>();

            Queue<GraphNode> queue = new LinkedList<>();
            queue.add(next);

            Set<GraphNode> done = new HashSet<>();
            done.add(next);

            while (!queue.isEmpty()) {
                GraphNode current = queue.poll();
                if (current.getNext().isEmpty()) {
                    ends.add(current);
                }
                for (GraphNode node : current.getNext()) {
                    if (!done.contains(node)) {
                        done.add(node);
                        queue.add(node);
                    }
                }
            }
        }
        return startOfProgram;
    }

    @Override
    public GraphNode visitWriteln(PascalParser.WritelnContext ctx) {
        visit(ctx.expression());
        return new GraphNode();
    }
}
