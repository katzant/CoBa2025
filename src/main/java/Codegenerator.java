public class Codegenerator extends PascalParserBaseVisitor<String> {

    private ScopeCodegen scopeCodegen;
    private String programName;
    private Type firstExpressionType;
    private int labelCount;

    public Codegenerator(ScopeCodegen scopeCodegen) {
        this.scopeCodegen = scopeCodegen;
    }

    @Override
    public String visitStart(PascalParser.StartContext ctx) {

        programName = ctx.program().IDENTIFIER().getText();

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

        String returnType = ctx.function_head().data_type().getText();
        Type type;

        String returnStatement;
        if (returnType.equals("integer")) {
            returnType = "I";
            returnStatement = "ireturn";
            scopeCodegen.addVariable(functionName, Type.INT);
            type = Type.INT;
        } else if (returnType.equals("boolean")) {
            returnType = "B";
            returnStatement = "ireturn";
            scopeCodegen.addVariable(functionName, Type.BOOLEAN);
            type = Type.BOOLEAN;
        } else if (returnType.equals("string")) {
            returnType = "Ljava/lang/String;";
            returnStatement = "areturn";
            scopeCodegen.addVariable(functionName, Type.STRING);
            type = Type.STRING;
        } else {
            returnType = "D";
            returnStatement = "dreturn";
            scopeCodegen.addVariable(functionName, Type.DOUBLE);
            type = Type.DOUBLE;
        }

        ScopeCodegen global = scopeCodegen;
        scopeCodegen = new ScopeCodegen(global);

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

        scopeCodegen.addVariable(functionName, type);

        String varArea;
        if (ctx.var_area() == null) {
            varArea = "";
        } else varArea = visit(ctx.var_area());

        String statements = visit(ctx.statements());

        String result;
        if (returnType.equals("D")) {
            result = "dload " + scopeCodegen.getIndex(functionName);
        } else if (returnType.equals("Ljava/lang/String;")) {
            result = "aload " + scopeCodegen.getIndex(functionName);
        } else result = "iload " + scopeCodegen.getIndex(functionName);

        String returnCode =  ".method public static " + functionName + "(" + parameters +")" + returnType +"\n" +
                ".limit stack 50\n" +
                ".limit locals 50\n" +
                varArea + "\n" +
                statements + "\n" +
                result + "\n" +
                returnStatement + "\n" +
                ".end method\n";

        scopeCodegen = global;
        return returnCode;
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

    @Override
    public String visitAdditExpression(PascalParser.AdditExpressionContext ctx) {
        String component0 = visit(ctx.expression(0));
        Type type0 = this.firstExpressionType;

        String component1 = visit(ctx.expression(1));
        Type type1 = this.firstExpressionType;

        if (ctx.PLUS() != null) {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                firstExpressionType = Type.DOUBLE;
                return component0 + "\n" + component1 + "\n" + "dadd\n";
            } else {
                firstExpressionType = Type.INT;
                return component0 + "\n" + component1 + "\n" + "iadd\n";
            }

        } else if (ctx.MINUS() != null) {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                firstExpressionType = Type.DOUBLE;
                return component0 + "\n" + component1 + "\n" + "dsub\n";
            } else {
                firstExpressionType = Type.INT;
                return component0 + "\n" + component1 + "\n" + "isub\n";
            }
        } else {
            firstExpressionType = Type.BOOLEAN;
            return component0 + "\n" + component1 + "\n" + "ior\n";
        }
    }

    @Override
    public String visitMultExpression(PascalParser.MultExpressionContext ctx) {
        String component0 = visit(ctx.expression(0));
        Type type0 = firstExpressionType;

        String component1 = visit(ctx.expression(1));
        Type type1 = firstExpressionType;
        if (ctx.MULT() != null) {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    firstExpressionType = Type.DOUBLE;
                    return component0 + "\n" + component1 + "\n" + "i2d\n" + "dmul\n";
                } else if (type0 != Type.DOUBLE) {
                    firstExpressionType = Type.DOUBLE;
                    return component0 +"\n" + "i2d\n" + component1 + "\n" + "dmul\n";
                } else {
                    firstExpressionType = Type.DOUBLE;
                    return component0 + "\n" + component1 + "\n" + "dmul\n";
                }
            } else {
                firstExpressionType = Type.INT;
                return component0 + "\n" + component1 + "\n" + "imul\n";
            }
        } else if (ctx.DIV() != null) {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    firstExpressionType = Type.DOUBLE;
                    return component0 + "\n" + component1 + "\n" + "i2d\n" + "ddiv\n";
                } else if (type0 != Type.DOUBLE) {
                    firstExpressionType = Type.DOUBLE;
                    return component0 +"\n" + "i2d\n" + component1 + "\n" + "ddiv\n";
                } else {
                    firstExpressionType = Type.DOUBLE;
                    return component0 + "\n" + component1 + "\n" + "ddiv\n";
                }
            } else {
                firstExpressionType = Type.INT;
                return component0 + "\n" + component1 + "\n" + "idiv\n";
            }
        } else if (ctx.MOD() != null) {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    firstExpressionType = Type.DOUBLE;
                    return component0 + "\n" + component1 + "\n" + "i2d\n" + "drem\n";
                } else if (type0 != Type.DOUBLE) {
                    firstExpressionType = Type.DOUBLE;
                    return component0 +"\n" + "i2d\n" + component1 + "\n" + "drem\n";
                } else {
                    firstExpressionType = Type.DOUBLE;
                    return component0 + "\n" + component1 + "\n" + "drem\n";
                }
            } else {
                firstExpressionType = Type.INT;
                return component0 + "\n" + component1 + "\n" + "irem\n";
            }
        } else {
            firstExpressionType = Type.BOOLEAN;
            return component0 + "\n" + component1 + "\n" + "iand\n";
        }
    }

    private String getBoolString(String component0, String component1, String instruction) {
        String result = component0 + "\n" + component1 + "\n" +
                instruction + " " + "trueLabel" + "_" + labelCount + "\n" +
                "bipush 0\n" +
                "goto " + "endLabel" + "_" + labelCount + "\n" +
                "trueLabel" + "_" + labelCount + ":\n" +
                "bipush 1\n" +
                "endLabel" + "_" + labelCount + ":\n";
        labelCount++;
        return result;
    }

    private String getBoolString(String component0, String component1, String instruction,
            String secondInstruction) {
        String result =  component0 + "\n" + component1 + "\n" +
                instruction + "\n" +
                secondInstruction + " " + "trueLabel" + "_" + labelCount + "\n" +
                "bipush 0\n" +
                "goto " + "endLabel" + "_" + labelCount + "\n" +
                "trueLabel" + "_" + labelCount + ":\n" +
                "bipush 1\n" +
                "endLabel" + "_" + labelCount + ":\n";
        labelCount++;
        return result;
    }

    @Override
    public String visitBoolExpression(PascalParser.BoolExpressionContext ctx) {
        String component0 = visit(ctx.expression(0));
        Type type0 = firstExpressionType;
        String component1 = visit(ctx.expression(1));
        Type type1 = firstExpressionType;

        if (ctx.EQUALS() != null) {
            if (type0 == Type.STRING) {
                return getBoolString(component0, component1, "if_acmpeq");
            }else if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    return getBoolString(component0, component1 + "\n i2d\n", "dcmpg", "ifeq");
                } else if (type0 != Type.DOUBLE) {
                    return getBoolString(component0 + "\n i2d\n", component1, "dcmpg", "ifeq");
                } else return getBoolString(component0, component1, "dcmpg", "ifeq");
            } else return getBoolString(component0, component1, "if_icmpeq");
        }
        else if (ctx.UNEQUAL() != null) {
            if (type0 == Type.STRING) {
                return getBoolString(component0, component1, "if_acmpne");
            } else if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    return getBoolString(component0, component1 + "\n i2d\n", "dcmpg", "ifne");
                } else if (type0 != Type.DOUBLE) {
                    return getBoolString(component0 + "\n i2d\n", component1, "dcmpg", "ifne");
                } else return getBoolString(component0, component1, "dcmpg", "ifne");
            } else return getBoolString(component0, component1, "if_icmpne");
        }
        else if (ctx.GREATER_EQUALS() != null) {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    return getBoolString(component0, component1 + "\n i2d\n", "dcmpg", "ifge");
                } else if (type0 != Type.DOUBLE) {
                    return getBoolString(component0 + "\n i2d\n", component1, "dcmpg", "ifge");
                } else return getBoolString(component0, component1, "dcmpg", "ifge");
            } else return getBoolString(component0, component1, "if_icmpge");
        }
        else if (ctx.LESS_EQUAL() != null) {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    return getBoolString(component0, component1 + "\n i2d\n", "dcmpl", "ifle");
                } else if (type0 != Type.DOUBLE) {
                    return getBoolString(component0 + "\n i2d\n", component1, "dcmpl", "ifle");
                } else return getBoolString(component0, component1, "dcmpl", "ifge");
            } else return getBoolString(component0, component1, "if_icmple");
        }
        else if (ctx.GREATHER_THAN() != null) {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    return getBoolString(component0, component1 + "\n i2d\n", "dcmpg", "ifgt");
                } else if (type0 != Type.DOUBLE) {
                    return getBoolString(component0 + "\n i2d\n", component1, "dcmpg", "ifgt");
                } else return getBoolString(component0, component1, "dcmpg", "ifge");
            } else return getBoolString(component0, component1, "if_icmpgt");
        }
        else {
            if (type0 == Type.DOUBLE || type1 == Type.DOUBLE) {
                if (type0 == Type.DOUBLE && type1 != Type.DOUBLE) {
                    return getBoolString(component0, component1 + "\n i2d\n", "dcmpg", "iflt");
                } else if (type0 != Type.DOUBLE) {
                    return getBoolString(component0 + "\n i2d\n", component1, "dcmpg", "iflt");
                } else return getBoolString(component0, component1, "dcmpg", "ifge");
            } else return getBoolString(component0, component1, "if_icmplt");
        }
    }

    @Override
    public String visitUnaryExpression(PascalParser.UnaryExpressionContext ctx) {
        String expression = visit(ctx.expression());
        Type type = firstExpressionType;
        if (ctx.NOT() != null) {
            return expression + "\n" + "bipush 1" + "\n" + "ixor";
        } else if (ctx.MINUS() != null) {
            if (type == Type.DOUBLE) {
                return expression + "\n" + "dneg";
            } else return expression + "\n" + "ineg";
        } else return expression;
    }

    @Override
    public String visitLiteral(PascalParser.LiteralContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            String identifier = ctx.IDENTIFIER().getText();
            TypeCodegen litType = scopeCodegen.get(identifier);
            if (litType.isLocal()) {
                if (litType.getType() == Type.DOUBLE) {
                    return "dload " + litType.getIndex() + "\n";
                } else if (litType.getType() == Type.INT || litType.getType() == Type.BOOLEAN) {
                    return "iload " + litType.getIndex() + "\n";
                } else return "aload " + litType.getIndex() + "\n";
            } else {
                if (litType.getType() == Type.INT) {
                    return "getstatic " + programName + "/" + identifier + " I\n";
                } else if (litType.getType() == Type.DOUBLE) {
                    return "getstatic " + programName + "/" + identifier + " D\n";
                } else if (litType.getType() == Type.BOOLEAN) {
                    return "getstatic " + programName + "/" + identifier + " B\n";
                } else return "getstatic " + programName + "/" + identifier + " Ljava/lang/String;\n";
            }
        } else if (ctx.INTCONST() != null) {
            return "ldc " + ctx.INTCONST().getText() + "\n";
        } else if (ctx.DOUBLECONST() != null) {
            return "ldc2_w " + ctx.DOUBLECONST().getText() + "\n";
        } else if (ctx.STRINGCONST() != null) {
            return "ldc " + ctx.STRINGCONST().getText() + "\n";
        } else if (ctx.TRUE() != null) {
            return "bipush 1\n";
        } else return "bipush 0\n";
    }

    @Override
    public String visitParenExpression(PascalParser.ParenExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public String visitWriteln(PascalParser.WritelnContext ctx) {
        String expression = visit(ctx.expression());
        Type type = firstExpressionType;
        if (type == Type.STRING) {
            return "getstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                    expression + "\n" +
                    "invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n";
        } else if (type == Type.DOUBLE) {
            return "getstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                    expression + "\n" +
                    "invokevirtual java/io/PrintStream/println(D)V\n";
        } else if (type == Type.BOOLEAN) {
            return "getstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                    expression + "\n" +
                    "invokevirtual java/io/PrintStream/println(Z)V\n";
        } else return "getstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                    expression + "\n" +
                    "invokevirtual java/io/PrintStream/println(I)V\n";
    }

    @Override
    public String visitFuncExpression(PascalParser.FuncExpressionContext ctx) {
        StringBuilder result = new StringBuilder();
        StringBuilder parameter = new StringBuilder();

        Type type;
        for (PascalParser.ExpressionContext expression : ctx.function_call().expression()) {
            result.append(visit(expression));
            type = firstExpressionType;
            if (type == Type.STRING) {
                parameter.append("Ljava/lang/String;");
            } else if (type == Type.DOUBLE) {
                parameter.append("D");
            } else parameter.append("I");
        }
        result.append("invokestatic " + programName + "/" + ctx.function_call().IDENTIFIER().getText());
        result.append("(" + parameter.toString() + ")");
        if (scopeCodegen.get(ctx.function_call().IDENTIFIER().getText()).getType() == Type.STRING) {
            result.append("Ljava/lang/String;");
        } else if (scopeCodegen.get(ctx.function_call().IDENTIFIER().getText()).getType() == Type.DOUBLE) {
            result.append("D");
        } else result.append("I");
        return result.toString();
    }

    @Override
    public String visitIf_statement(PascalParser.If_statementContext ctx) {
        int internalLabel = labelCount++;
        if (ctx.statement().size() == 1) {
            return visit(ctx.expression()) + "\n" +
                    "ifeq endLabel_" + internalLabel + "\n" +
                    visit(ctx.statement(0)) + "\n" +
                    "endLabel_" + internalLabel + ":\n";
        } else {
            return visit(ctx.expression()) + "\n" +
                    "ifeq elseLabel_" + internalLabel + "\n" +
                    visit(ctx.statement(0)) + "\n" +
                    "goto endLabel_" + internalLabel + "\n" +
                    "elseLabel_" + internalLabel + ":\n" +
                    visit(ctx.statement(1)) + "\n" +
                    "endLabel_" + internalLabel + ":\n";
        }
    }

    @Override
    public String visitWhile_statement(PascalParser.While_statementContext ctx) {
        int internalLabel = labelCount++;
        String content = "";

        if (ctx.statement() != null) {
            content = visit(ctx.statement());
        } else if (ctx.begin_end_block() != null) {
            content = visit(ctx.begin_end_block());
        }

        return "startLabel_" + internalLabel + ":\n" +
                visit(ctx.expression()) + "\n" +
                "ifeq " + "endLabel_" + internalLabel + "\n" +
                content + "\n" +
                "goto " + "startLabel_" + internalLabel + "\n" +
                "endLabel_" + internalLabel + ":\n";
    }

    @Override
    public String visitStatement(PascalParser.StatementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitStatements(PascalParser.StatementsContext ctx) {
        StringBuilder result = new StringBuilder();

        for (PascalParser.StatementContext statement : ctx.statement()) {
            String code = visit(statement);
            if (code != null) {
                result.append(code + "\n");
            }
        }

        return result.toString();
    }

    @Override
    public String visitBegin_end_block(PascalParser.Begin_end_blockContext ctx) {
        return visit(ctx.statements());
    }

    @Override
    public String visitProcedure(PascalParser.ProcedureContext ctx) {
        String functionName = ctx.procedure_head().IDENTIFIER().getText();

        StringBuilder parameters = new StringBuilder();
        if (ctx.procedure_head().parameter() != null) {
            for (PascalParser.Param_tupleContext tuple : ctx.procedure_head().parameter().param_tuple()) {
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

        String varArea;
        if (ctx.var_area() == null) {
            varArea = "";
        } else varArea = visit(ctx.var_area());

        return ".method public static " + functionName + " (" + parameters +")V" +"\n" +
                ".limit stack 50\n" +
                ".limit locals 50\n" +
                varArea + "\n" +
                visit(ctx.statements()) + "\n" +
                "return\n" +
                ".end method\n";
    }
}
