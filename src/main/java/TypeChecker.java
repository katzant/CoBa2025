import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class TypeChecker extends PascalParserBaseVisitor<Type> {

    private Scope scope;

    public TypeChecker(Scope scope) {
        this.scope = new Scope(null);
    }

    @Override
    public Type visitVar_declaration(PascalParser.Var_declarationContext ctx) {
        Type var = visitData_type(ctx.data_type());
        for (TerminalNode variable : ctx.IDENTIFIER()) {
            if (!scope.addVariable(variable.getText(), var)) {
                System.out.println("Type Error: " + variable.getSymbol().getLine() + ":" +
                        variable.getSymbol().getCharPositionInLine() + " " +
                        variable.getSymbol().getText());
            }
        }
        return null;
    }

    @Override
    public Type visitParam_tuple(PascalParser.Param_tupleContext ctx) {
        Type var = visitData_type(ctx.data_type());
        for (TerminalNode variable : ctx.IDENTIFIER()) {
            if (!scope.addVariable(variable.getText(), var)) {
                System.out.println("Type Error: " + variable.getSymbol().getLine() + ":" +
                        variable.getSymbol().getCharPositionInLine() + " " +
                        variable.getSymbol().getText());
            }
        }
        return null;
    }

    @Override
    public Type visitAdditExpression(PascalParser.AdditExpressionContext ctx) {
        Type var1 = visit(ctx.expression(0));
        Type var2 = visit(ctx.expression(1));
        if (ctx.OR() != null) {
            if (var1 == Type.BOOLEAN && var2 == Type.BOOLEAN) {
                return Type.BOOLEAN;
            } else {
                System.out.println("Type Error: " + ctx.OR().getSymbol().getLine() + ":" +
                        ctx.OR().getSymbol().getCharPositionInLine() + " boolean expected");
                return null;
            }
        } else {
            if (ctx.PLUS() != null || ctx.MINUS() != null) {
                if (var1 == Type.INT && var2 == Type.INT) {
                    return Type.INT;
                } else if (var1 == Type.INT && var2 == Type.DOUBLE) {
                    return Type.DOUBLE;
                } else if (var1 == Type.DOUBLE && var2 == Type.INT) {
                    return Type.DOUBLE;
                } else if (var1 == Type.DOUBLE && var2 == Type.DOUBLE) {
                    return Type.DOUBLE;
                } else {
                    if (ctx.PLUS() != null) {
                        System.out.println("Type Error: " + ctx.PLUS().getSymbol().getLine() + ":" +
                                ctx.PLUS().getSymbol().getCharPositionInLine() + " int or double expected");
                    } else {
                        if (ctx.MINUS() != null) {
                            System.out.println("Type Error: " + ctx.MINUS().getSymbol().getLine() + ":" +
                                    ctx.MINUS().getSymbol().getCharPositionInLine() + " int or double expected");
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Type visitMultExpression(PascalParser.MultExpressionContext ctx) {
        Type var1 = visit(ctx.expression(0));
        Type var2 = visit(ctx.expression(1));
        if (ctx.AND() != null) {
            if (var1 == Type.BOOLEAN && var2 == Type.BOOLEAN) {
                return Type.BOOLEAN;
            } else {
                System.out.println("Type Error: " + ctx.AND().getSymbol().getLine() + ":" +
                        ctx.AND().getSymbol().getCharPositionInLine() + " boolean expected");
                return null;
            }
        } else if (ctx.MULT() != null) {
                if (var1 == Type.INT && var2 == Type.INT) {
                    return Type.INT;
                } else if (var1 == Type.INT && var2 == Type.DOUBLE) {
                    return Type.DOUBLE;
                } else if (var1 == Type.DOUBLE && var2 == Type.INT) {
                    return Type.DOUBLE;
                } else if (var1 == Type.DOUBLE && var2 == Type.DOUBLE) {
                    return Type.DOUBLE;
                } else {
                    System.out.println("Type Error: " + ctx.MULT().getSymbol().getLine() + ":" +
                            ctx.MULT().getSymbol().getCharPositionInLine() + " int or double expected");
                }
            } else if (ctx.DIV() != null) {
            if ((var1 == Type.INT && var2 == Type.INT)
                    || (var1 == Type.INT && var2 == Type.DOUBLE)
                    || (var1 == Type.DOUBLE && var2 == Type.INT)
                    || (var1 == Type.DOUBLE && var2 == Type.DOUBLE)) {
                return Type.DOUBLE;
            } else {
                System.out.println("Type Error: " + ctx.DIV().getSymbol().getLine() + ":" +
                        ctx.DIV().getSymbol().getCharPositionInLine() + " int or double expected");
            }
        } else if (ctx.MOD() != null) {
            if ((var1 == Type.INT && var2 == Type.INT)) {
                return Type.INT;
            } else {
                System.out.println("Type Error: " + ctx.MOD().getSymbol().getLine() + ":" +
                        ctx.MOD().getSymbol().getCharPositionInLine() + " int expected");
            }
        }
        return null;
    }

    @Override
    public Type visitBoolExpression(PascalParser.BoolExpressionContext ctx) {
        Type var1 = visit(ctx.expression(0));
        Type var2 = visit(ctx.expression(1));
        boolean check = var1 == Type.INT && var2 == Type.INT
                || var1 == Type.INT && var2 == Type.DOUBLE
                || var1 == Type.DOUBLE && var2 == Type.INT
                || var1 == Type.DOUBLE && var2 == Type.DOUBLE;
        if (ctx.LESS_THAN() != null) {
            if (check) {
                return Type.BOOLEAN;
            } else {
                System.out.println("Type Error: " + ctx.LESS_THAN().getSymbol().getLine() + ":" +
                        ctx.LESS_THAN().getSymbol().getCharPositionInLine() + " int or double expected");
            }
        } else if (ctx.GREATHER_THAN() != null) {
            if (check) {
                return Type.BOOLEAN;
            } else {
                System.out.println("Type Error: " + ctx.GREATHER_THAN().getSymbol().getLine() + ":" +
                        ctx.GREATHER_THAN().getSymbol().getCharPositionInLine() + " int or double expected");
            }
        } else if (ctx.LESS_EQUAL() != null) {
            if (check) {
                return Type.BOOLEAN;
            } else {
                System.out.println("Type Error: " + ctx.LESS_EQUAL().getSymbol().getLine() + ":" +
                        ctx.LESS_EQUAL().getSymbol().getCharPositionInLine() + " int or double expected");
            }
        } else if (ctx.GREATER_EQUALS() != null) {
            if (check) {
                return Type.BOOLEAN;
            } else {
                System.out.println("Type Error: " + ctx.GREATER_EQUALS().getSymbol().getLine() + ":" +
                        ctx.GREATER_EQUALS().getSymbol().getCharPositionInLine() + " int or double expected");
            }
        } else {
            boolean check_equal = check
                    || var1 == Type.BOOLEAN && var2 == Type.BOOLEAN
                    || var1 == Type.STRING && var2 == Type.STRING;
            if (ctx.EQUALS() != null) {
                if (check_equal) {
                    return Type.BOOLEAN;
                } else {
                    System.out.println("Type Error: " + ctx.EQUALS().getSymbol().getLine() + ":" +
                            ctx.EQUALS().getSymbol().getCharPositionInLine() + " Expected: int = int, double = int," +
                            " int = double, double = double, boolean = boolean, string = string");
                }
            } else if (ctx.UNEQUAL() != null) {
                if (check_equal) {
                    return Type.BOOLEAN;
                } else {
                    System.out.println("Type Error: " + ctx.UNEQUAL().getSymbol().getLine() + ":" +
                            ctx.EQUALS().getSymbol().getCharPositionInLine() + " Expected: int <> int, double <> int," +
                            " int <> double, double <> double, boolean <> boolean, string <> string");
                }
            }
        }
        return null;
    }

    @Override
    public Type visitAssignment(PascalParser.AssignmentContext ctx) {
        Type var = visit(ctx.expression());
        Type assignedType = scope.get(ctx.IDENTIFIER().getText());
        if (assignedType == null) {
            System.out.println("Type Error: " + ctx.IDENTIFIER().getText() + " not found");
            return null;
        }
        if (assignedType != var ) {
            if (assignedType == Type.DOUBLE &&  var == Type.INT) {
                return assignedType;
            }
            System.out.println("Type Error: " + ctx.IDENTIFIER().getSymbol().getLine() + ":" +
                    ctx.IDENTIFIER().getSymbol().getCharPositionInLine() + " cannot assign " + var + " to " + assignedType.toString());
            return null;
        }
        return assignedType;
    }

    @Override
    public Type visitIf_statement(PascalParser.If_statementContext ctx) {
        Type expression = visit(ctx.expression());
        if (expression == null) {
            return null;
        }
        if (expression != Type.BOOLEAN) {
            System.out.println("Type Error: " + ctx.IF_SYMBOL().getSymbol().getLine() + ":" +
                    ctx.IF_SYMBOL().getSymbol().getCharPositionInLine()+
                    " Expected boolean but got: " + expression.toString());
            return null;
        }
        visit(ctx.statement(0));
        if (ctx.statement(1) != null)  {
            visit(ctx.statement(1));
        }
        return null;
    }

    @Override
    public Type visitWhile_statement(PascalParser.While_statementContext ctx) {
        Type expression = visit(ctx.expression());
        if (expression == null) {
            return null;
        }
        if (expression != Type.BOOLEAN) {
            System.out.println("Type Error: " + ctx.WHILE_SYMBOL().getSymbol().getLine() + ":" +
                    ctx.WHILE_SYMBOL().getSymbol().getCharPositionInLine() +
                    " Expected boolean but got: " + expression.toString());
            return null;
        }
        if (ctx.statement() != null) {
            visit(ctx.statement());
        } else visit(ctx.begin_end_block());
        return null;
    }

    @Override
    public Type visitStatements(PascalParser.StatementsContext ctx) {
        for (PascalParser.StatementContext statementContext : ctx.statement()) {
            visit(statementContext);
        }
        return null;
    }

    @Override
    public Type visitWriteln(PascalParser.WritelnContext ctx) {
        visit(ctx.expression());
        return null;
    }

    @Override
    public Type visitFuncExpression(PascalParser.FuncExpressionContext ctx) {
        return visit(ctx.function_call());
    }

    @Override
    public Type visitFunction_call(PascalParser.Function_callContext ctx) {
        String functionName = ctx.IDENTIFIER().getText();
        Function function = scope.getFunction(functionName);
        if (function == null) {
            System.out.println("Type Error: called function" + functionName + " not found");
            return null;
        }
        List<Type> parameters = function.getParameters();

        if (parameters.size() != ctx.expression().size()) {
            System.out.println("Type Error: number of parameters in function call does not match");
            return null;
        }

        for (int i = 0; i < parameters.size(); i++) {
            Type functionType = parameters.get(i);
            Type calledType = visit(ctx.expression(i));

            if (!(functionType == calledType) && !(functionType == Type.DOUBLE && calledType == Type.INT)) {
                System.out.println("Type Error: parameter with index " + i +
                         " used in function call "+ functionName + " does not match");
                return null;
            }
        }

        return function.getReturnValue();
    }

    @Override
    public Type visitFunction(PascalParser.FunctionContext ctx) {
        String functionName = ctx.function_head().IDENTIFIER().getText();
        Type returnValue = visit(ctx.function_head().data_type());

        List<Type> parameters = new ArrayList<>();
        if (ctx.function_head().parameter() != null) {

            for (PascalParser.Param_tupleContext tuple : ctx.function_head().parameter().param_tuple()) {
                for (int i = 0; i < tuple.IDENTIFIER().size(); i++) {
                    parameters.add(visit(tuple.data_type()));
                }
            }
        }
        Function function = new Function(parameters, returnValue);
        scope.addFunction(functionName, function);
        scope = new Scope(scope);
        scope.addVariable(ctx.function_head().IDENTIFIER().getText(), returnValue);

        if (ctx.function_head().parameter() != null) {
            visit(ctx.function_head().parameter());
        }
        if (ctx.var_area() != null) {
            visit(ctx.var_area());
        }
        visit(ctx.statements());

        scope = scope.getParent();
        return returnValue;
    }

    @Override
    public Type visitLiteral(PascalParser.LiteralContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            String identifier =  ctx.IDENTIFIER().getText();
            Type litType = scope.get(identifier);
            if (litType == null) {
                System.out.println("Type Error: " + ctx.IDENTIFIER().getSymbol().getLine() +
                        ":" + ctx.IDENTIFIER().getSymbol().getCharPositionInLine() +
                        " variable \"" + identifier + "\" not found");
            }
            return litType;
        } else if (ctx.INTCONST() != null) {
            return Type.INT;
        } else if (ctx.DOUBLECONST() != null) {
            return Type.DOUBLE;
        }  else if (ctx.STRINGCONST() != null) {
            return Type.STRING;
        } else return Type.BOOLEAN;
    }

    @Override
    public Type visitData_type(PascalParser.Data_typeContext ctx) {
        if (ctx == null) {
            System.out.println("Type Error: data type not found");
            return null;
        }
        if (ctx.INT_SYMBOL() != null) {
            return Type.INT;
        } else if (ctx.DOUBLE_SYMBOL() != null) {
            return Type.DOUBLE;
        } else if (ctx.STRING_SYMBOL() != null) {
            return Type.STRING;
        } else if (ctx.BOOL_SYMBOL() != null) {
            return Type.BOOLEAN;
        }

        return null;
    }

    @Override
    public Type visitProcedure(PascalParser.ProcedureContext ctx) {
        String functionName = ctx.procedure_head().IDENTIFIER().getText();

        List<Type> parameters = new ArrayList<>();
        if (ctx.procedure_head().parameter() != null) {
            for (PascalParser.Param_tupleContext tuple : ctx.procedure_head().parameter().param_tuple()) {
                for (int i = 0; i < tuple.IDENTIFIER().size(); i++) {
                    parameters.add(visit(tuple.data_type()));
                }
            }
        }
        Function function = new Function(parameters, Type.VOID);
        scope.addFunction(functionName, function);
        scope = new Scope(scope);

        if (ctx.procedure_head().parameter() != null) {
            visit(ctx.procedure_head().parameter());
        }
        if (ctx.var_area() != null) {
            visit(ctx.var_area());
        }
        visit(ctx.statements());

        scope = scope.getParent();
        return null;
    }
}

    // TODO: WELCHE FEATURES FEHLEN NOCH IN MEINER IMPLEMENTATION?
