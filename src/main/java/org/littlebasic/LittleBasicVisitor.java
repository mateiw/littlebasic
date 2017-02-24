package org.littlebasic;

import basic.LBExpressionParser;
import basic.LittleBasicBaseVisitor;
import basic.LittleBasicParser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * The ANTLR visitor. This does the actual job of executing the program.
 */
public class LittleBasicVisitor extends LittleBasicBaseVisitor<Value> {

    private InputStream stdin;
    private PrintStream stdout;
    private PrintStream stderr;
    private Memory memory;

    private PrintStream printStream;
    private BufferedReader inputStream;

    public LittleBasicVisitor(Memory memory, InputStream stdin, PrintStream stdout, PrintStream stderr) {
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
        this.memory = memory;
    }

    @Override
    public Value visitProg(LittleBasicParser.ProgContext ctx) {
        init();
        try {
            return super.visitProg(ctx);
        } finally {
            cleanup();
        }
    }

    private void init() {
        printStream = new PrintStream(stdout, true);
        inputStream = new BufferedReader(new InputStreamReader(stdin));
    }

    private void cleanup() {
        printStream.close();
    }

    @Override
    public Value visitString(LittleBasicParser.StringContext ctx) {
        String value = ctx.getText();
        return new Value(value.substring(1, value.length() - 1));
    }

    @Override
    public Value visitNumber(LittleBasicParser.NumberContext ctx) {
        return new Value(Long.parseLong(ctx.getText()));
    }

    @Override
    public Value visitId(LittleBasicParser.IdContext ctx) {
        String id = ctx.getText();
        return memory.get(id);
    }

    @Override
    public Value visitLetstmt(LittleBasicParser.LetstmtContext ctx) {
        String varname = ctx.vardecl().varname().ID().getText();
        Value value = visit(ctx.expression());
        memory.assign(varname, value);
        return value;
    }

    @Override
    public Value visitMulDivExpr(LittleBasicParser.MulDivExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        if (ctx.op.getType() == LBExpressionParser.MUL) {
            return left.mul(right);
        } else if (ctx.op.getType() == LBExpressionParser.DIV) {
            return left.div(right);
        } else {
            return left.mod(right);
        }
    }

    @Override
    public Value visitAddSubExpr(LittleBasicParser.AddSubExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        if (ctx.op.getType() == LBExpressionParser.ADD) {
            return left.add(right);
        } else {
            return left.sub(right);
        }
    }

    @Override
    public Value visitLenfunc(LittleBasicParser.LenfuncContext ctx) {
        Value arg = visit(ctx.expression());
        if (arg.isString()) {
            return new Value(arg.internalString().length());
        } else {
            throw new TypeException("Couldn't evaluate LEN(). Argument is not a string");
        }
    }

    @Override
    public Value visitValfunc(LittleBasicParser.ValfuncContext ctx) {
        Value arg = visit(ctx.expression());
        if (arg.isString()) {
            String str = arg.internalString();
            try {
                return new Value(Long.parseLong(str));
            } catch (NumberFormatException e) {
                return Value.NaN;
            }
        }
        return arg;
    }

    @Override
    public Value visitIsnanfunc(LittleBasicParser.IsnanfuncContext ctx) {
        Value arg = visit(ctx.expression());
        return arg.isNaN() ? Value.TRUE : Value.FALSE;
    }

    @Override
    public Value visitStatement(LittleBasicParser.StatementContext ctx) {
        try {
            return super.visitStatement(ctx);
        } catch (TypeException e) {
            addLocation(e, ctx);

            throw e;
        }
    }

    @Override
    public Value visitRelExpr(LittleBasicParser.RelExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        switch (ctx.op.getType()) {
            case LBExpressionParser.GT:
                return left.gt(right);
            case LBExpressionParser.GTE:
                return left.gte(right);
            case LBExpressionParser.LT:
                return left.lt(right);
            case LBExpressionParser.LTE:
                return left.lte(right);
            case LBExpressionParser.EQ:
                return left.eq(right);
            default:
                return left.neq(right);
        }
    }

    private void addLocation(InterpreterException ex, ParserRuleContext ctx) {
        ex.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }

    @Override
    public Value visitNotExpr(LittleBasicParser.NotExprContext ctx) {
        Value value = visit(ctx.expression());
        return value.not();
    }

    @Override
    public Value visitAndExpr(LittleBasicParser.AndExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        return left.and(right);
    }

    @Override
    public Value visitOrExpr(LittleBasicParser.OrExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        return left.or(right);
    }

    @Override
    public Value visitExpExpr(LittleBasicParser.ExpExprContext ctx) {
        Value left = visit(ctx.expression(0));
        Value right = visit(ctx.expression(1));
        // TODO which one is left and which is right ?
        return left.exp(right);
    }

    @Override
    public Value visitIfstmt(LittleBasicParser.IfstmtContext ctx) {
        Value condition = visit(ctx.expression());
        if (condition.isTrue()) {
            return visit(ctx.block());
        } else {
            for(LittleBasicParser.ElifstmtContext elifCtx : ctx.elifstmt()) {
                condition = visit(elifCtx.expression());
                if (condition.isTrue()) {
                    return visit(elifCtx.block());
                }
            }
            if (ctx.elsestmt() != null) {
                return visit(ctx.elsestmt().block());
            }
        }
        return condition;
    }

    @Override
    public Value visitPrintstmt(LittleBasicParser.PrintstmtContext ctx) {
        Value value = visit(ctx.expression());
        if (value.isNumber()) {
            printStream.println(value.internalNumber());
        } else {
            printStream.println(value.internalString());
        }
        return value;
    }

    @Override
    public Value visitInputstmt(LittleBasicParser.InputstmtContext ctx) {
        printStream.print(visit(ctx.string()).internalString() + " ");
        String varname = ctx.vardecl().getText();
        try {
            String line = inputStream.readLine();
            Value val = new Value(line);
            memory.assign(varname, val);
            return val;
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    @Override
    public Value visitForstmt(LittleBasicParser.ForstmtContext ctx) {
        String varname = ctx.vardecl().varname().ID().getText();
        Value start = visit(ctx.expression(0));
        Value end = visit(ctx.expression(1));
        Value step = ctx.expression(2) != null ? visit(ctx.expression(2)) : new Value(1);
        for (long i = start.internalNumber(); i <= end.internalNumber(); i = i + step.internalNumber()) {
            memory.assign(varname, new Value(i));
            try {
                visit(ctx.block());
            } catch (ContinueLoopException e) {
                continue;
            } catch (ExitLoopException e) {
                break;
            }
        }
        return new Value(0);
    }

    @Override
    public Value visitWhilestmt(LittleBasicParser.WhilestmtContext ctx) {
        Value cond = visit(ctx.expression());
        while (cond.isTrue()) {
            try {
                visit(ctx.block());
            } catch (ContinueLoopException e) {
                continue;
            } catch (ExitLoopException e) {
                break;
            } finally {
                cond = visit(ctx.expression());
            }
        }
        return new Value(0);
    }

    @Override
    public Value visitRepeatstmt(LittleBasicParser.RepeatstmtContext ctx) {
        Value cond;
        do {
            try {
                visit(ctx.block());
            } catch (ContinueLoopException e) {
                continue;
            } catch (ExitLoopException e) {
                break;
            } finally {
                cond = visit(ctx.expression());
            }
        } while (cond.isFalse());
        return new Value(0);
    }

    @Override
    public Value visitContinuestmt(LittleBasicParser.ContinuestmtContext ctx) {
        throw new ContinueLoopException();
    }

    @Override
    public Value visitExitstmt(LittleBasicParser.ExitstmtContext ctx) {
        throw new ExitLoopException();
    }

}
