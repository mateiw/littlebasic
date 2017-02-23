package org.littlebasic;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;

/**
 * Created by matei on 2/23/17.
 */
public class LittleBasicTest {

    private void test(String resource, BiConsumer<Interpreter, String> assertions) {
        try {
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            Interpreter interpreter = new Interpreter(System.in, stdout, System.err);
            interpreter.run(resource(resource));
            String output = new String(stdout.toByteArray());
            assertions.accept(interpreter, output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream resource(String filename) {
        return getClass().getResourceAsStream("/" + filename);
    }

    @Test
    public void testPrint() {
        test("print.bas", (interpreter, output) -> {
            assertEquals("Hello world!\n", output);
        });
    }

    @Test
    public void testLet() {
        test("let.bas", (interpreter, output) -> {
            assertEquals(true, interpreter.getMemory().get("string").isString());
            assertEquals("foo", interpreter.getMemory().get("string").internalString());
            assertEquals(true, interpreter.getMemory().get("numeric").isNumber());
            assertEquals(123L, interpreter.getMemory().get("numeric").internalNumber());
        });
    }

    @Test
    public void testForSimple() {
        test("for_simple.bas", (interpreter, output) -> {
            assertEquals("1\n2\n3\n4\n5\n", output);
        });
    }

    @Test
    public void testForContinue() {
        test("for_continue.bas", (interpreter, output) -> {
            assertEquals("3\n4\n5\n", output);
        });
    }

    @Test
    public void testForExit() {
        test("for_exit.bas", (interpreter, output) -> {
            assertEquals("1\n2\n3\n", output);
        });
    }

    @Test
    public void testIfSimpleTrue() {
        test("if_simple_true.bas", (interpreter, output) -> {
            assertEquals("one\ntwo\n", output);
        });
    }

    @Test
    public void testIfSimpleFalse() {
        test("if_simple_false.bas", (interpreter, output) -> {
            assertEquals("three\n", output);
        });
    }

    @Test
    public void testIfElse() {
        test("if_else.bas", (interpreter, output) -> {
            assertEquals("true\nfalse\n", output);
        });
    }

}
