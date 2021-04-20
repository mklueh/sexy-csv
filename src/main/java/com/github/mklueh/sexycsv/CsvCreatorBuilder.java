package com.github.mklueh.sexycsv;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Generic Builder class template
 *
 * @param <Entity>
 * @param <Child>
 * @param <Builder>
 */
abstract class CsvCreatorBuilder<Entity, Child extends CsvCreator<Entity>,
        Builder extends CsvCreatorBuilder<Entity, Child, Builder>> {

    public abstract Child build();

    public abstract Builder withTypeConverter(Class<?> type, Function<Object, String> converter);

    public abstract Builder withEntity(Class<? extends Entity> clazz);

    public abstract Builder delimiter(String delimiter);

    public abstract CsvCreatorBuilderImpl<Entity, Child> prependHeader(Stream<Row> preHeader);
}