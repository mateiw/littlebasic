package org.littlebasic;

import basic.LBExpressionParser;
import basic.LittleBasicBaseVisitor;
import basic.LittleBasicParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;

/**
 * Created by matei on 2/21/17.
 */
public class LittleBasicVisitor extends LittleBasicBaseVisitor<Value> {

    private InputStream stdin;
    private OutputStream stdout;
    private OutputStream stderr;
    private Memory memory;

    private PrintStream printStream;
    private BufferedReader inputStream;

    public LittleBasicVisitor(Memory memory, InputStream stdin, OutputStream stdout, OutputStream stderr) {
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
        } else {
            return left.div(right);
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
    public Value visitIntfunc(LittleBasicParser.IntfuncContext ctx) {
        Value arg = visit(ctx.expression());
        return arg.toNumber();
    }

    @Override
    public Value visitLenfunc(LittleBasicParser.LenfuncContext ctx) {
        Value arg = visit(ctx.expression());
        return arg.stringLength();
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
            memory.assign(varname, new Value(line));
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO
        }
        return new Value(0);
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
            } catch (ContinueForException e) {
                continue;
            } catch (ExitForException e) {
                break;
            }
        }
        return new Value(0);
    }

    @Override
    public Value visitContinueForStmt(LittleBasicParser.ContinueForStmtContext ctx) {
        throw new ContinueForException();
    }

    @Override
    public Value visitExitForStmt(LittleBasicParser.ExitForStmtContext ctx) {
        throw new ExitForException();
    }
}
