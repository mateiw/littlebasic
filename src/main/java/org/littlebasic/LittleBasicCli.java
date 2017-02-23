package org.littlebasic;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by matei on 2/21/17.
 */
public class LittleBasicCli {

    public static void main(String[] args) {
        InputStream in = null;
        try {
            if (args.length == 1) {
                in = new FileInputStream(args[0]);
            } else {
                in = System.in;
            }

            Interpreter interpreter = new Interpreter(System.in, System.out, System.err);
            interpreter.run(in);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }


    }

}
