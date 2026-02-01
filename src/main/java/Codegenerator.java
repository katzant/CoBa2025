public class Codegenerator extends PascalParserBaseVisitor<String> {

    @Override
    public String visitAdditExpression(PascalParser.AdditExpressionContext ctx) {
        String component0 = visit(ctx.expression(0));
        String component1 = visit(ctx.expression(1));
        if (ctx.PLUS() != null) {
            return component0 + "\n" + component1 + "\n" + "iadd";
        } else if (ctx.MINUS() != null) {
            return component0 + "\n" + component1 + "\n" + "isub";
        } else {
            return component0 + "\n" + component1 + "\n" + "ior";
        }
    }
}
