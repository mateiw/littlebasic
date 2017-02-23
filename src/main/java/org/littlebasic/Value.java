package org.littlebasic;

import java.util.function.BiFunction;

/**
 * Created by matei on 2/23/17.
 */
public class Value {

    public static final Value FALSE = new Value(0);
    public static final Value TRUE = new Value(1);

    private Object value;

    public Value(String value) {
        this.value = value;
    }

    public Value(long value) {
        this.value = value;
    }

    public long internalNumber() {
        return (long)value;
    }

    public String internalString() {
        return (String)value;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isNumber() {
        return value instanceof Long;
    }

    public boolean isTrue() {
        assertNumber();
        return internalNumber() != 0;
    }

    private boolean isFalse() {
        assertNumber();
        return internalNumber() == 0;
    }

    private void assertNumber() {
        if (!isNumber()) {
            throw new TypeException(value + " not a number");
        }
    }

    private void assertString() {
        if (!isString()) {
            throw new TypeException(value + " not a string");
        }
    }

    public Value mul(Value right) {
        return arithmeticEval(right, (l, r) -> l * r);
    }

    public Value div(Value right) {
        return arithmeticEval(right, (l, r) -> l / r);
    }

    public Value add(Value right) {
        // TODO string + string/number concat
        return arithmeticEval(right, (l, r) -> l + r);
    }

    public Value sub(Value right) {
        return arithmeticEval(right, (l, r) -> l - r);
    }

    private Value arithmeticEval(Value right, BiFunction<Long, Long, Long> oper) {
        assertNumber();
        right.assertNumber();
        return new Value(oper.apply(internalNumber(), right.internalNumber()));
    }

    public Value toNumber() {
        if (isString()) {
            long v = Long.parseLong(internalString());
            return new Value(v);
        } else {
            return this;
        }
    }

    public Value gt(Value right) {
        return relEval(right, (l, r) -> l > r);
    }

    public Value gte(Value right) {
        return relEval(right, (l, r) -> l >= r);
    }

    public Value lt(Value right) {
        return relEval(right, (l, r) -> l < r);
    }

    public Value lte(Value right) {
        return relEval(right, (l, r) -> l <= r);
    }

    public Value eq(Value right) {
        if (isNumber() && right.isNumber()) {
            return relEval(right, (l, r) -> l == r);
        } else if (isString() && right.isString()) {
            return internalString().equals(right.internalString()) ? TRUE : FALSE;
        }
        return FALSE;
    }

    public Value neq(Value right) {
        Value eq = eq(right);
        return eq.equals(TRUE) ? FALSE : TRUE;
    }

    private Value relEval(Value right, BiFunction<Long, Long, Boolean> comparison) {
        assertNumber();
        right.assertNumber();
        if (comparison.apply(internalNumber(), right.internalNumber())) {
            return TRUE;
        }
        return FALSE;
    }

    public Value not() {
        assertNumber();
        if (internalNumber() == 0) {
            return TRUE;
        }
        return FALSE;
    }

    public Value and(Value right) {
        return isTrue() && right.isTrue() ? TRUE : FALSE;
    }

    public Value or(Value right) {
        return isTrue() || right.isTrue() ? TRUE : FALSE;
    }

    public Value exp(Value right) {
        assertNumber();
        right.assertNumber();
        return new Value(Math.round(Math.pow(internalNumber(), right.internalNumber())));
    }

    public Value stringLength() {
        assertString();
        return new Value(internalString().length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Value)) return false;

        Value value1 = (Value) o;

        return value != null ? value.equals(value1.value) : value1.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
