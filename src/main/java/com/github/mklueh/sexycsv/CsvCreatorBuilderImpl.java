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
public class CsvCreatorBuilderImpl<Entity, Child extends CsvCreator<Entity>>
        extends CsvCreatorBuilder<Entity, Child, CsvCreatorBuilderImpl<Entity, Child>> {

    protected String delimiter = ",";

    protected Class<? extends Entity> clazz;

    protected final Map<Class<?>, Function<Object, String>> typeConverters = new HashMap<>();

    private final Child creator;

    public CsvCreatorBuilderImpl(Child creator) {
        this.creator = creator;
    }

    @Override
    public CsvCreatorBuilderImpl<Entity, Child> withEntity(Class<? extends Entity> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public CsvCreatorBuilderImpl<Entity, Child> delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    @Override
    public Child build() {

        if (clazz == null)
            throw new RuntimeException("SexyCsv.Creator requires setting the entity class with .withEntity()");

        creator.typeConverters = typeConverters;
        creator.delimiter = delimiter;

        HeaderBuilder headerBuilder = new HeaderBuilder();
        HeaderBuilder.Header header = headerBuilder.fromEntity(clazz);
        creator.setHeader(header);

        return creator;
    }

    @Override
    public CsvCreatorBuilderImpl<Entity, Child> withTypeConverter(Class<?> type, Function<Object, String> converter) {
        this.typeConverters.put(type, converter);
        return this;
    }
}