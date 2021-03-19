package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

    /**
     * Disable exception thrown by duplicated header names
     */
    private boolean allowDuplicatedHeaders;

    private Charset charset;

    /**
     * Auto header detection
     */
    private boolean hasHeader;

    private HeaderBuilder.Header header = new HeaderBuilder.Header();

    /**
     * Filter corrupt rows
     */
    private Predicate<? super String> rowFilter;

    /**
     * Handle cell splitting by yourself
     */
    private Function<? super String, String[]> tokenizer;

    /**
     * Entity clazz
     */
    private Class<?> withEntity;


    public static <T, G> List<G> fromArrayToList(T[] a, Function<T, G> mapperFunction) {
        return Arrays.stream(a)
                .map(mapperFunction)
                .collect(Collectors.toList());
    }

    public <T, G> Stream<G> parse(Path path, Class<? extends T> clazz) throws IOException {
        Stream<Row> rows = parse(path);
        return rows.map(row -> {
            try {
                G o = (G) clazz.getDeclaredConstructor().newInstance();
                Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Type type = field.getGenericType();
                    CSVColumn annotation = field.getAnnotation(CSVColumn.class);
                    if (annotation != null) {
                        String columnName = annotation.value();
                        String value = row.get(columnName);
                        try {
                            if (type.getTypeName().equals(String.class.getName())) {
                                field.set(o, value);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                });
                return o;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

    }

    public Stream<Row> parse(Path path) throws IOException {
        if (hasHeader) {
            if (withEntity != null) {
                header = new HeaderBuilder().fromEntity(withEntity);
            } else loadHeader(path);
        }

        AtomicInteger line = new AtomicInteger();

        return Files.lines(path, charset())
                .skip(skipRows + (hasHeader ? 1 : 0))
                .filter(rowFilter != null ? rowFilter : s -> true)
                .map(tokenizer != null ? tokenizer : s -> s.split(delimiter))
                .map(cells -> {
                    Row row = new Row(line.getAndIncrement(), allowDuplicatedHeaders);
                    for (int i = 0; i < cells.length; i++) {
                        String h = null;
                        if (header != null) h = header.headers.get(i);
                        row.addCell(h, cells[i]);
                    }
                    return row;
                });
    }

    protected void loadHeader(Path path) throws IOException {
        Files.lines(path, charset())
                .skip(skipRows)
                .findFirst()
                .ifPresent(s -> header.headers = Arrays.asList(tokenizer != null ? tokenizer.apply(s) : s.split(delimiter)));
    }

    private Charset charset() {
        return charset != null ? charset : Charset.defaultCharset();
    }


}