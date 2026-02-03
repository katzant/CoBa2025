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
    public String visitVar_area(PascalParser.Var_areaContext ctx) {
        StringBuilder varDeclarations = new StringBuilder();
        for (PascalParser.Var_declarationContext declaration : ctx.var_declaration()) {
            varDeclarations.append(visit(declaration));
        }

        return varDeclarations.toString();
    }

    @Override
    public String visitVar_declaration(PascalParser.Var_declarationContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        if (scopeCodegen.getParent() == null) {
            for (int i = 0; i < ctx.IDENTIFIER().size(); i++) {
                if (ctx.data_type().INT_SYMBOL() != null) {
                    scopeCodegen.addVariable(ctx.IDENTIFIER(i).getText(), Type.INT);
                    stringBuilder.append(".field public static ").append(ctx.IDENTIFIER(i).getText()).append(" I\n");
                } else if (ctx.data_type().DOUBLE_SYMBOL() != null) {
                    scopeCodegen.addVariable(ctx.IDENTIFIER(i).getText(), Type.DOUBLE);
                    stringBuilder.append(".field public static ").append(ctx.IDENTIFIER(i).getText()).append(" D\n");
                } else if (ctx.data_type().BOOL_SYMBOL() != null) {
                    scopeCodegen.addVariable(ctx.IDENTIFIER(i).getText(), Type.BOOLEAN);
                    stringBuilder.append(".field public static ").append(ctx.IDENTIFIER(i).getText()).append(" B\n");
                } else {
                    scopeCodegen.addVariable(ctx.IDENTIFIER(i).getText(), Type.STRING);
                    stringBuilder.append(".field public static ").append(ctx.IDENTIFIER(i).getText()).append(" Ljava/lang/String;\n");
                }
            }
        } else {
            for (int i = 0; i < ctx.IDENTIFIER().size(); i++) {
                if (ctx.data_type().INT_SYMBOL() != null) {
                    scopeCodegen.addVariable(ctx.IDENTIFIER(i).getText(), Type.INT);
                } else if (ctx.data_type().DOUBLE_SYMBOL() != null) {
                    scopeCodegen.addVariable(ctx.IDENTIFIER(i).getText(), Type.DOUBLE);
                } else if (ctx.data_type().BOOL_SYMBOL() != null) {
                    scopeCodegen.addVariable(ctx.IDENTIFIER(i).getText(), Type.BOOLEAN);
                } else {
                    scopeCodegen.addVariable(ctx.IDENTIFIER(i).getText(), Type.STRING);
                }
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public String visitAssignment(PascalParser.AssignmentContext ctx) {
        if (scopeCodegen.getParent() == null) {
            if (scopeCodegen.get(ctx.IDENTIFIER().getText()).getType() == Type.INT) {
                return visit(ctx.expression()) + "\n" +
                        "putstatic " + programName + "/" + ctx.IDENTIFIER().getText() + " I";
            } else if (scopeCodegen.get(ctx.IDENTIFIER().getText()).getType() == Type.DOUBLE) {
                return visit(ctx.expression()) + "\n" +
                        "putstatic " + programName + "/" + ctx.IDENTIFIER().getText() + " D";
            } else if(scopeCodegen.get(ctx.IDENTIFIER().getText()).getType() == Type.BOOLEAN) {
                return visit(ctx.expression()) + "\n" +
                        "putstatic " + programName + "/" + ctx.IDENTIFIER().getText() + " B";
            } else return visit(ctx.expression()) + "\n" +
                    "putstatic " + programName + "/" + ctx.IDENTIFIER().getText() + " Ljava/lang/String;";
        } else {
            if (scopeCodegen.get(ctx.IDENTIFIER().getText()).getType() == Type.INT) {
                return visit(ctx.expression()) + "\n" +
                        "istore " + scopeCodegen.getIndex(ctx.IDENTIFIER().getText());
            } else if (scopeCodegen.get(ctx.IDENTIFIER().getText()).getType() == Type.DOUBLE) {
                return visit(ctx.expression()) + "\n" +
                        "dstore " + scopeCodegen.getIndex(ctx.IDENTIFIER().getText());
            } else if(scopeCodegen.get(ctx.IDENTIFIER().getText()).getType() == Type.BOOLEAN) {
                return visit(ctx.expression()) + "\n" +
                        "istore " + scopeCodegen.getIndex(ctx.IDENTIFIER().getText());
            } else return visit(ctx.expression()) + "\n" +
                    "astore " + scopeCodegen.getIndex(ctx.IDENTIFIER().getText());
        }
    }

//    @Override
//    public String visitLiteral(PascalParser.LiteralContext ctx) {
//        if (ctx.IDENTIFIER() != null) {
//            String identifier = ctx.IDENTIFIER().getText();
//            TypeCodegen litType = scopeCodegen.get(identifier);
//            if (litType.isLocal()) {
//                return "iload" + litType.getIndex();
//            } else return "getstatic " + programName + "/" + identifier + "/" + litType.getType();
//        } else if (ctx.INTCONST() != null) {
//            return "bipush " + ctx.INTCONST().getText();
//        } else if (ctx.DOUBLECONST() != null) {
//            return "ldc2_w " + ctx.DOUBLECONST().getText();
//        } else if (ctx.STRINGCONST() != null) {
//            return "ldc " + ctx.STRINGCONST().getText();
//        } else if (ctx.TRUE() != null) {
//            return "bipush 1";
//        } else return "bipush 0";
//    }
//
//    @Override
//    public String visitBoolExpression(PascalParser.BoolExpressionContext ctx) {
//
//    }

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

//    @Override String visitIf_statement(PascalParser.If_statementContext ctx) {
//        String labelElse = visit(ctx.ELSE_SYMBOL());
//        String labelEnd = ...
//        String ifComponent0 = visit(ctx.expression());
//        if (visit(ctx.ELSE_SYMBOL() != null)) {
//            return
//        }
//    }
}
