package com.github.mklueh.sexycsv;

import java.util.function.Function;

/**
 * Generic Builder class template
 *
 * @param <Entity>
 * @param <Child>
 * @param <Builder>
 */
abstract class CsvParserBuilder<Entity, Child extends CsvParser,
        Builder extends CsvParserBuilder<Entity, Child, Builder>> {

    public abstract Child build();

    public abstract Builder withTypeConverter(Class<?> type, Function<Object, String> converter);

    public abstract Builder withEntity(Class<? extends Entity> clazz);

    public abstract Builder delimiter(String delimiter);
}