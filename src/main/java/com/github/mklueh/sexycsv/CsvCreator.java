package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Creates CSV data from entities
 *
 * @param <Entity>
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
class CsvCreator<Entity> {

    String delimiter;

    private HeaderBuilder.Header header;

    Map<Class<?>, Function<Object, String>> typeConverters = new HashMap<>();

    /**
     * Adding type converters for custom data types, allowing to customize string conversion.
     * Example: Enum types where the string representation differs from the actual enum
     *
     * @param type      the type class
     * @param converter function receiving the type and returning
     */
    public <T> void addTypeConverter(Class<T> type, Function<Object, String> converter) {
        this.typeConverters.put(type, converter);
    }

    public void toFile(Collection<Entity> collection, Path path) {
        Stream<Row> rowStream = toRowStream(collection);
        toFile(path, rowStream);
    }

    public void toFile(Stream<Entity> collection, Path path) {
        Stream<Row> rowStream = toRowStream(collection);
        toFile(path, rowStream);
    }

    @SneakyThrows
    private void toFile(Path path, Stream<Row> rowStream) {
        File file = path.toFile();
        try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8)) {
            rowStream.forEachOrdered(row -> pw.println(convertRowToString(row)));
        }
    }

    public String toString(Collection<Entity> collection) {
        return convertRowStreamToString(toRowStream(collection));
    }

    public Stream<Row> toRowStream(Collection<Entity> entities) {
        return toRowStream(entities.stream());
    }

    public String toString(Stream<Entity> stream) {
        return convertRowStreamToString(toRowStream(stream));
    }

    public Stream<String> toStream(Stream<Entity> stream) {
        return toRowStream(stream).map(this::convertRowToString);
    }

    private String convertRowStreamToString(Stream<Row> rowStream) {
        return convertToCSV(rowStream).toString();
    }

    private StringBuilder convertToCSV(Stream<Row> rowStream) {
        return rowStream.reduce(new StringBuilder(), (s, recPosition) -> {
            s.append(convertRowToString(recPosition));
            s.append("\n");
            return s;
        }, (s, s2) -> s);
    }

    protected String convertRowToString(Row recPosition) {
        return String.join(delimiter, Arrays.asList(recPosition.getCells()));
    }

    private Stream<Row> toRowStream(Stream<Entity> entities) {
        AtomicInteger line = new AtomicInteger(0);
        Row headerRow = createHeaderRow(header);
        Stream<? extends Row> header = Stream.of(headerRow);
        return Stream.concat(header, entities.map(e -> {
            Row row = parseEntity(e);
            row.setLine(line.getAndIncrement());
            return row;
        }));
    }

    /**
     * Converts any entity to a Row by parsing csv-relevant fields based on the {@link CSVColumn} annotation
     */
    protected Row parseEntity(Entity entity) {
        Row row = new Row(0, new String[header.headers.size()]);
        Arrays.stream(entity.getClass().getDeclaredFields())
                .forEach(field -> {
                    field.setAccessible(true);
                    if (field.getAnnotation(CSVColumn.class) != null) {
                        String identifier = extractIdentifier(field);
                        Integer position = header.headerPositionMap.get(identifier);

                        try {
                            Object obj = field.get(entity);
                            Class<?> type = field.getType();
                            if (typeConverters.containsKey(type)) {
                                Function<Object, String> fun = typeConverters.get(type);
                                row.getCells()[position] = obj == null ? null : String.valueOf(fun.apply(obj));
                            } else row.getCells()[position] = obj == null ? null : String.valueOf(obj);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        return row;
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
     * Creates the header row
     */
    protected Row createHeaderRow(HeaderBuilder.Header header) {
        return new Row(0, header.headerPositionMapInverse.values().toArray(new String[header.headers.size()]));
    }


}