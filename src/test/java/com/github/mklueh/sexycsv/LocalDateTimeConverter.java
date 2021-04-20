package com.github.mklueh.sexycsv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class LocalDateTimeConverter implements Function<Object, String> {

    static DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public String apply(Object localDateTime) {
        return ((LocalDateTime) localDateTime).format(formatter);
    }

}
