package com.github.mklueh.sexycsv;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SexyCSV {

    /**
     * Skips rows above the actual header
     */
    private int skipRows;

    private String delimiter;

    private Charset charset;
    
    /**
     * Auto header detection
     */
    private boolean hasHeader;

    private List<String> header;

    /**
     * Filter corrupt rows
     */
    private Predicate<? super String> rowFilter;

    /**
     * Handle cell splitting by yourself
     */
    private Function<? super String, String[]> tokenizer;

    public Stream<Row> parse(Path path) throws IOException {
        if (hasHeader) {
            loadHeader(path);
        }

        AtomicInteger line = new AtomicInteger();

        return Files.lines(path, charset())
                .skip(skipRows + (hasHeader ? 1 : 0))
                .filter(rowFilter != null ? rowFilter : s -> true)
                .map(tokenizer != null ? tokenizer : s -> s.split(delimiter))
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

    protected void loadHeader(Path path) throws IOException {
        Files.lines(path, charset())
                .skip(skipRows)
                .findFirst()
                .ifPresent(s -> header = Arrays.asList(tokenizer != null ? tokenizer.apply(s) : s.split(delimiter)));
    }
    
     private Charset charset() {
        return charset != null ? charset : Charset.defaultCharset();
    }

}
