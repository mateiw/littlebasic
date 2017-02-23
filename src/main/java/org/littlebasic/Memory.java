package org.littlebasic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matei on 2/23/17.
 */
public class Memory {

    private Map<String, Value> memory = new HashMap<>();

    public Value get(String name) {
        return memory.get(name);
    }

    public void assign(String name, Value value) {
        memory.put(name, value);
    }

    public void free() {
        memory.clear();
    }
}
