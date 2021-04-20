package com.github.mklueh.sexycsv;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Builder class template implementation. Set builder methods here
 *
 * @param <Entity>
 * @param <Child>
 */
public class CsvCreatorBuilderImpl<Entity, Child extends CsvCreator<Entity>>
        extends CsvCreatorBuilder<Entity, Child, CsvCreatorBuilderImpl<Entity, Child>> {

    protected String delimiter = ",";

    protected Stream<Row> preHeader;

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


    /**
     * If you like to prepend rows above the actual header
     */
    @Override
    public CsvCreatorBuilderImpl<Entity, Child> prependHeader(Stream<Row> preHeader) {
        this.preHeader = preHeader;
        return this;
    }

    @Override
    public Child build() {

        if (clazz == null)
            throw new RuntimeException("SexyCsv.Creator requires setting the entity class with .withEntity()");

        creator.typeConverters = typeConverters;
        creator.delimiter = delimiter;

        HeaderBuilder headerBuilder = new HeaderBuilder(Charset.defaultCharset());
        HeaderBuilder.Header header = headerBuilder.fromEntity(clazz);

        creator.setHeader(header);
        creator.setPrependingHeader(preHeader);

        return creator;
    }

    @Override
    public CsvCreatorBuilderImpl<Entity, Child> withTypeConverter(Class<?> type, Function<Object, String> converter) {
        this.typeConverters.put(type, converter);
        return this;
    }
}