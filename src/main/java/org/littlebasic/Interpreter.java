package org.littlebasic;

import basic.LittleBasicLexer;
import basic.LittleBasicParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by matei on 2/23/17.
 */
public class Interpreter {

    private InputStream stdin;
    private OutputStream stdout;
    private OutputStream stderr;
    private Memory memory;

    public Interpreter(InputStream stdin, OutputStream stdout, OutputStream stderr) {
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public void run(InputStream progrIn) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(progrIn);
        LittleBasicLexer lexer = new LittleBasicLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LittleBasicParser parser = new LittleBasicParser(tokens);

        ParseTree tree = parser.prog();

        memory = new Memory();
        LittleBasicVisitor eval = new LittleBasicVisitor(memory, stdin, stdout, stderr);
        eval.visit(tree);
    }

    public Memory getMemory() {
        return memory;
    }

    public void clear() {
        memory.free();
    }
}
