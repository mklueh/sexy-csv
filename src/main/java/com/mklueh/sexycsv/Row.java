package com.mklueh.sexycsv;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Row {
    private int line;
    private final Map<String, String> cellsByHeader = new HashMap<>();
    private final List<String> cellsByIndex = new ArrayList<>();

    public void addCell(String header, String value) {
        if (header != null)
            this.cellsByHeader.put(header, value);
        this.cellsByIndex.add(value);
    }

    public String get(String name) {
        return cellsByHeader.get(name);
    }

    public String get(int i) {
        return cellsByIndex.get(i);
    }
}