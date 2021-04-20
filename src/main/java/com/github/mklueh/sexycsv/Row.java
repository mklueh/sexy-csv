package com.github.mklueh.sexycsv;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Row container
 */
@Getter
@Setter
public class Row {

    /**
     * Line number
     */
    private int line;

    //TODO unify if possible
    private String[] cells;
    private final List<String> cellsByIndex = new ArrayList<>();

    //By storing the values with their corresponding column names, we make access efficient
    private final Map<String, String> cellsByHeader = new HashMap<>();

    public Row(int line, String[] cells) {
        this.line = line;
        this.cells = cells;
    }

    public Row(int line) {
        this.line = line;
    }

    @SneakyThrows
    public void addCell(String header, String value) {
        this.cellsByIndex.add(value);
        if (header != null) {
            if (this.cellsByHeader.containsKey(header)) {
                int pos = this.cellsByIndex.size() - 1;
                header = header + "#" + pos;
            }
            this.cellsByHeader.put(header, value);
        }
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