package com.github.mklueh.sexycsv;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Combining the {@link CsvCreator} and {@link CsvParser} in one API
 */
public class SexyCSV {

    /**
     * Create CSV based on given entities
     */
    @NoArgsConstructor
    public static class Creator<Entity> extends CsvCreator<Entity> {
        public static <Entity> CsvCreatorBuilderImpl<Entity, Creator<Entity>> builder() {
            return new CsvCreatorBuilderImpl<>(new Creator<>());
        }
    }

    /**
     * Extract data from CSV and create entities
     */
    @SuperBuilder
    @SuppressWarnings("ALL")
    public static class Parser extends CsvParser {

    }
}