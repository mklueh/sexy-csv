package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by m.kluehspies on 16/03/2021
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CsvParser {

    /**
     * Skips rows above the actual header
     */
    private int skipRows;

    private String delimiter;

    private Charset charset;

    /**
     * Auto header detection based on the first row of
     * the CSV file
     */
    private boolean hasHeaderRow;

    private HeaderBuilder.Header header;

    /**
     * Filter corrupt rows
     */
    private Predicate<? super String> rowFilter;

    /**
     * Handle cell splitting by yourself
     */
    private Function<? super String, String[]> tokenizer;

    public <T> Stream<T> parse(Path path, Class<T> clazz) throws IOException {
        Stream<Row> rows = parseFile(path, clazz);
        return rows.map(row -> {
            try {
                T o = (T) clazz.getDeclaredConstructor().newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Type type = field.getGenericType();
                    CSVColumn annotation = field.getAnnotation(CSVColumn.class);
                    if (annotation != null) {
                        String columnName = annotation.value();
                        String value = row.get(columnName);
                        if (type.getTypeName().equals(String.class.getName())) {
                            field.set(o, value);
                        }
                    }
                }
                return o;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }


    public Stream<Row> parse(Path path) throws IOException {
        return parseFile(path, null);
    }

    private <T> Stream<Row> parseFile(Path path, Class<T> entityClass) throws IOException {

        header = loadHeader(path, entityClass);

        AtomicInteger line = new AtomicInteger();

        return Files.lines(path, charset())
                .skip(skipRows + (hasHeaderRow ? 1 : 0))
                .filter(rowFilter != null ? rowFilter : s -> true)
                .map(tokenizer != null ? tokenizer : s -> s.split(delimiter))
                .map(cells -> {
                    Row row = new Row(line.getAndIncrement());
                    for (int i = 0; i < cells.length; i++) {
                        String h = null;
                        if (header != null) h = header.headers.get(i);
                        row.addCell(h, cells[i]);
                    }
                    return row;
                });
    }

    protected <T> HeaderBuilder.Header loadHeader(Path path, Class<T> entityClass) throws IOException {
        HeaderBuilder headerBuilder = new HeaderBuilder(charset());
        if (header != null) {
            return header;
        }

        if (entityClass != null) {
            return headerBuilder.fromEntity(entityClass);
        }

        if (hasHeaderRow)
            return headerBuilder.fromFile(path, skipRows, delimiter, tokenizer);

        return new HeaderBuilder.Header();
    }

    private Charset charset() {
        return charset != null ? charset : Charset.defaultCharset();
    }


}