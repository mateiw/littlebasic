package org.littlebasic;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.PrintStream;

/**
 * Created by matei on 2/24/17.
 */
public class ErrorListener extends BaseErrorListener {

    private PrintStream stderr;

    public ErrorListener(PrintStream stderr) {
        this.stderr = stderr;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line,
                            int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        stderr.println(msg);
    }
}
