package org.littlebasic;

/**
 * Utility methods.
 */
public class Utils {

    public static String formatErrorMessage(int line, int posInLine, String message) {
        return "Error at [" + line + ", " + posInLine + "]: " + message;
    }

}
