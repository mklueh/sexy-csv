package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Builds a header by analyzing a given entity
 */
public class HeaderBuilder {

    private final Charset charset;

    public HeaderBuilder(Charset charset) {
        this.charset = charset;
    }

    public static class Header {
        AtomicInteger fieldNumberCount = new AtomicInteger(0);
        Map<String, Integer> headerPositionMap = new HashMap<>();
        Map<Integer, String> headerPositionMapInverse = new HashMap<>();
        List<String> headers = new ArrayList<>();

        public boolean coveredBy(Header otherHeader) {
            return false;
        }
    }

    /**
     * Returns the column identifier based on the annotation definition or field
     * name as fallback
     */
    private String extractIdentifier(Field field) {
        CSVColumn annotation = field.getAnnotation(CSVColumn.class);
        return CSVColumn.NULL.equals(annotation.value()) ? field.getName() : annotation.value();
    }

    /**
     * Should provide an ongoing column number based on defined positions, as well as the natural field
     * order as fallback.
     *
     * TODO handle overlapping positions correctly or fail?
     * TODO fallback ordering not implemented correctly
     */
    private int determineColumnPosition(Header header, CSVColumn annotation) {
        int fieldNum = header.fieldNumberCount.incrementAndGet(); //TODO use natural order as fallback of overlapping
        int position = annotation.position();
        return position == -1 ? header.headers.size() : position;
    }

    /**
     * Builds the header from the first row of the given file
     */
    public Header fromFile(Path path, int skipRows, String delimiter, Function<? super String, String[]> tokenizer) throws IOException {
        Header header = new Header();
        return Files.lines(path, charset)
                .skip(skipRows)
                .findFirst()
                .map(s -> {
                    header.headers = Arrays.asList(tokenizer != null ? tokenizer.apply(s) : s.split(delimiter));
                    return header;
                }).orElse(header);
    }

    /**
     * Extracts headers based on the {@link CSVColumn} annotations defined in the entity and orders them by position
     * or by natural field order as fallback
     */
    public Header fromEntity(Class<?> entity) {
        Header header = new Header();
        Arrays.stream(entity.getDeclaredFields()).forEach(field -> {
            field.setAccessible(true);
            CSVColumn annotation = field.getAnnotation(CSVColumn.class);
            if (annotation != null) {

                String columnName = extractIdentifier(field);
                int nextPosition = determineColumnPosition(header, annotation);

                header.headerPositionMap.put(columnName, nextPosition);
                header.headerPositionMapInverse.put(nextPosition, columnName);
                header.headers.add(columnName);

            }
        });

        return header;
    }

}
