package com.github.mklueh.sexycsv;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Row {

    private int line;

    private String[] cells;
    private boolean allowDuplicatedHeaders;

    private final Map<String, String> cellsByHeader = new HashMap<>();
    private final List<String> cellsByIndex = new ArrayList<>();

    public Row(int line, boolean allowDuplicatedHeaders) {
        this.line = line;
        this.allowDuplicatedHeaders = allowDuplicatedHeaders;
    }

    public Row(int line, String[] cells) {
        this.line = line;
        this.cells = cells;
    }

    @SneakyThrows
    public void addCell(String header, String value) {
        if (header != null) {
            if (!allowDuplicatedHeaders && this.cellsByHeader.containsKey(header))
                throw new Exception("Header " + header + " exists multiple times");
            this.cellsByHeader.put(header, value);
        }
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