package org.littlebasic;

/**
 * Created by matei on 2/24/17.
 */
public class InterpreterException extends RuntimeException {

    private int line;
    private int posInLine;

    public InterpreterException(String message) {
        super(message);
    }

    public void setLocation(int line, int posInLine) {
        this.line = line;
        this.posInLine = posInLine;
    }

    @Override
    public String getMessage() {
        return Utils.formatErrorMessage(line, posInLine, super.getMessage());
    }
}
