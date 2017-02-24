package org.littlebasic;

/**
 * Created by matei on 2/24/17.
 */
public class Utils {

    public static String formatErrorMessage(int line, int posInLine, String message) {
        return "Error at [" + line + ", " + posInLine + "]: " + message;
    }

}
