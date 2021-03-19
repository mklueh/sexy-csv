package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builds a header by analyzing a given entity
 */
public class HeaderBuilder {


    public static class Header {
        AtomicInteger fieldNumberCount = new AtomicInteger(0);
        Map<String, Integer> headerPositionMap = new HashMap<>();
        Map<Integer, String> headerPositionMapInverse = new HashMap<>();
        List<String> headers = new ArrayList<>();
    }

    /**
     * Returns the column identifier based on the annotation definition or field
     * name as fallback
     */
    private String extractIdentifier(java.lang.reflect.Field field) {
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
