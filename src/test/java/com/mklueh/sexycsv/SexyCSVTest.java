package com.mklueh.sexycsv;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SexyCSVTest {


    @Test
    void testDefault() throws IOException {
        Path path = Paths.get("src", "test", "resources", "sample-data.csv");

        SexyCSV parser = SexyCSV.builder()
                .delimiter(",")
                .hasHeader(true) //auto-use of the given header
                //.header(Arrays.asList("id", "name", "age", "country")) set optional header
                .skipRows(3)
                .rowFilter(s -> s.matches("^\\d.*")) //we are only interested in rows that start with a number
                //.tokenizer(s -> s.split(";")) optional custom tokenizer
                .build();

        List<Row> data = parser.parse(path)
                .collect(Collectors.toList());

        assertEquals(3, data.size());
    }


    @Test
    void testTab() throws IOException {
        Path path = Paths.get("src", "test", "resources", "sample-data-tab.csv");

        SexyCSV parser = SexyCSV.builder()
                .delimiter("\t")
                .hasHeader(true) //auto-use of the given header
                //.header(Arrays.asList("id", "name", "age", "country")) set optional header
                .skipRows(3)
                .rowFilter(s -> s.matches("^\\d.*")) //we are only interested in rows that start with a number
                .build();

        List<Row> data = parser.parse(path)
                .collect(Collectors.toList());


        assertEquals(3, data.size());

        for (Row row : data) {
            assertNotNull(row.get(0));
            assertNotNull(row.get(1));
            assertNotNull(row.get(2));
            if (row.line() != 2)
                assertNotNull(row.get(3));
            else assertNull(row.get(3));
        }


    }
}