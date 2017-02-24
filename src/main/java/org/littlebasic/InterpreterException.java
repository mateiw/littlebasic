package org.littlebasic;

/**
 * Base exception for interpreter runtime errors.
 */
public abstract class InterpreterException extends RuntimeException {

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
