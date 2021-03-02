package com.github.mklueh.sexycsv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Row {
    private final int line;
    private final Map<String, String> cellsByHeader = new HashMap<>();
    private final List<String> cellsByIndex = new ArrayList<>();

    public Row(int line) {
        this.line = line;
    }

    public void addCell(String header, String value) {
        if (header != null)
            this.cellsByHeader.put(header, value);
        this.cellsByIndex.add(value);
    }

    public String get(String name) {
        return cellsByHeader.get(name);
    }

    public String get(int i) {
        return i < this.cellsByIndex.size() ? cellsByIndex.get(i) : null;
    }

    public int line() {
        return line;
    }
}