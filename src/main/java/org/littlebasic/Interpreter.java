package org.littlebasic;

import basic.LittleBasicLexer;
import basic.LittleBasicParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * The entry point of the interpreter.
 */
public class Interpreter {

    private InputStream stdin;
    private OutputStream stdout;
    private OutputStream stderr;
    private PrintStream stdoutPrint;
    private PrintStream stderrPrint;
    private Memory memory;

    public Interpreter(InputStream stdin, OutputStream stdout, OutputStream stderr) {
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
        this.stdoutPrint = new PrintStream(stdout, true);
        this.stderrPrint = new PrintStream(stderr, true);
    }

    public Value run(InputStream progrIn) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(progrIn);
        LittleBasicLexer lexer = new LittleBasicLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LittleBasicParser parser = new LittleBasicParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        parser.removeErrorListeners();
        parser.addErrorListener(new ErrorListener(stderrPrint));
        try {
            ParseTree tree = parser.prog();
            memory = new Memory();
            LittleBasicVisitor eval = new LittleBasicVisitor(memory, stdin, stdoutPrint, stderrPrint);
            eval.visit(tree);
        } catch (InterpreterException e) {
            stderrPrint.println(e.getMessage());
        } catch (ParseCancellationException e) {
            if (e.getCause() instanceof InputMismatchException) {
                InputMismatchException inputEx = (InputMismatchException)e.getCause();
                String msg = Utils.formatErrorMessage(
                        inputEx.getOffendingToken().getLine(),
                        inputEx.getOffendingToken().getCharPositionInLine(),
                        "Syntax error");
                stderrPrint.println(msg);
            }
        }
        return null;
    }

    public Memory getMemory() {
        return memory;
    }

    public void clear() {
        memory.free();
    }
}
