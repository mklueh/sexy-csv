package com.github.mklueh.sexycsv;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Builder class template implementation. Set builder methods here
 *
 * @param <Entity>
 * @param <Child>
 */
public class CsvParserBuilderImpl<Entity, Child extends CsvParser>
        extends CsvParserBuilder<Entity, Child, CsvParserBuilderImpl<Entity, Child>> {

    protected String delimiter = ",";

    protected Class<? extends Entity> clazz;

    protected final Map<Class<?>, Function<Object, String>> typeConverters = new HashMap<>();

    private final Child parser;

    public CsvParserBuilderImpl(Child parser) {
        this.parser = parser;
    }

    @Override
    public CsvParserBuilderImpl<Entity, Child> withEntity(Class<? extends Entity> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public CsvParserBuilderImpl<Entity, Child> delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    @Override
    public Child build() {

        if (clazz == null)
            throw new RuntimeException("SexyCsv.Creator requires setting the entity class with .withEntity()");

        return parser;
    }

    @Override
    public CsvParserBuilderImpl<Entity, Child> withTypeConverter(Class<?> type, Function<Object, String> converter) {
        this.typeConverters.put(type, converter);
        return this;
    }
}