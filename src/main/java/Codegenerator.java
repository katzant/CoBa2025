public class Codegenerator extends PascalParserBaseVisitor<String> {

    private ScopeCodegen scopeCodegen;
    private String programName;

    public Codegenerator(ScopeCodegen scopeCodegen) {
        this.scopeCodegen = scopeCodegen;
    }

    @Override
    public String visitStart(PascalParser.StartContext ctx) {
        StringBuilder functionsProcedures = new StringBuilder();
        for (PascalParser.FunctionContext function : ctx.function()) {
            functionsProcedures.append(visit(function));
        }
        for (PascalParser.ProcedureContext procedure : ctx.procedure()) {
            functionsProcedures.append(visit(procedure));
        }

        String varArea;
        if (ctx.var_area() == null) {
            varArea = "";
        } else varArea = visit(ctx.var_area());

        return visit(ctx.program()) + "\n" +
                varArea +
                ".method public <init>()V\n" +
                "aload_0\n" +
                "invokenonvirtual java/lang/Object/<init>()V\n" +
                "return\n" +
                ".end method\n" +
                functionsProcedures.toString() + "\n" +
                ".method public static main([Ljava/lang/String;)V\n" +
                ".limit stack 50\n" +
                ".limit locals 50\n" +
                visit(ctx.statements()) + "\n" +
                "return\n" +
                ".end method";
    }

    @Override
    public String visitProgram(PascalParser.ProgramContext ctx) {
        programName = ctx.IDENTIFIER().getText();
        return ".class public " + ctx.IDENTIFIER().getText() + "\n"
                + ".super java/lang/Object";
    }

    @Override
    public String visitFunction(PascalParser.FunctionContext ctx) {
        String functionName = ctx.function_head().IDENTIFIER().getText();

        StringBuilder parameters = new StringBuilder();
        if (ctx.function_head().parameter() != null) {
            for (PascalParser.Param_tupleContext tuple : ctx.function_head().parameter().param_tuple()) {
                for (int i = 0; i < tuple.IDENTIFIER().size(); i++) {
                    if (tuple.data_type().getText().equals("integer")) {
                        parameters.append("I");
                        scopeCodegen.addVariable(tuple.IDENTIFIER(i).getText(), Type.INT);
                    } else if (tuple.data_type().getText().equals("double")) {
                        parameters.append("D");
                        scopeCodegen.addVariable(tuple.IDENTIFIER(i).getText(), Type.DOUBLE);
                    } else if (tuple.data_type().getText().equals("string")) {
                        parameters.append("Ljava/lang/String;");
                        scopeCodegen.addVariable(tuple.IDENTIFIER(i).getText(), Type.STRING);
                    } else {
                        parameters.append("B");
                        scopeCodegen.addVariable(tuple.IDENTIFIER(i).getText(), Type.BOOLEAN);
                    }
                }
            }
        }

        String returnType = visit(ctx.function_head().data_type());
        String returnStatement;
        if (returnType.equals("integer")) {
            returnType = "I";
            returnStatement = "ireturn";
        } else if (returnType.equals("boolean")) {
            returnType = "B";
            returnStatement = "ireturn";
        } else if (returnType.equals("string")) {
            returnType = "Ljava/lang/String;";
            returnStatement = "areturn";
        } else {
            returnType = "D";
            returnStatement = "dreturn";
        }

        String varArea;
        if (ctx.var_area() == null) {
            varArea = "";
        } else varArea = visit(ctx.var_area());

        return ".method public static " + functionName + " (" + parameters +")" + returnType +"\n" +
                ".limit stack 50\n" +
                ".limit locals 50\n" +
                varArea + "\n" +
                visit(ctx.statements()) + "\n" +
                returnStatement + "\n" +
                ".end method\n";
    }

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

    @Override
    public String visitMultExpression(PascalParser.MultExpressionContext ctx) {
        String component0 = visit(ctx.expression(0));
        String component1 = visit(ctx.expression(1));
        if (ctx.MULT() != null) {
            return component0 + "\n" + component1 + "\n" + "imult";
        } else if (ctx.DIV() != null) {
            return component0 + "\n" + component1 + "\n" + "idiv";
        } else if (ctx.MOD() != null) {
            return component0  + "\n" + component1 + "\n" + "irem";
        } else {
            return component0 + "\n" + component1 + "\n" + "iand";
        }
    }
}
