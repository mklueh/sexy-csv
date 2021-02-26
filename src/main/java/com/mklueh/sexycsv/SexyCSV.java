package com.mklueh.sexycsv;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SexyCSV {

    private int skipRows;

    private String delimiter;

    private boolean hasHeader;

    private List<String> header;

    private Predicate<? super String> rowFilter;



    public Stream<Row> parse(Path path) throws IOException {
        if (hasHeader) {
            loadHeader(path);
        }

        AtomicInteger line = new AtomicInteger();
        Stream<String> stream = getStream(path);

        return stream
                .filter(rowFilter != null ? rowFilter : s -> true)
                .map(s -> s.split(delimiter))
                .map(cells -> {
                    Row row = new Row(line.getAndIncrement());
                    for (int i = 0; i < cells.length; i++) {
                        String h = null;
                        if (header != null) h = header.get(i);
                        row.addCell(h, cells[i]);
                    }
                    return row;
                });
    }

    private void loadHeader(Path path) throws IOException {
        getStream(path)
                .findFirst()
                .ifPresent(s -> header = Arrays.asList(s.split(delimiter)));
    }

    private Stream<String> getStream(Path path) throws IOException {
        Stream<String> stream = Files.lines(path);
        stream = stream.skip(skipRows);
        return stream;
    }

}
