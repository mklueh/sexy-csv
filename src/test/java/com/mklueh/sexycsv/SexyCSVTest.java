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
    void test() throws IOException {
        Path path = Paths.get("src", "test", "resources", "sample-data.csv");

        SexyCSV parser = SexyCSV.builder()
                .delimiter(",")
                .hasHeader(true) //auto-use of the given header
                //.header(Arrays.asList("id", "name", "age", "country")) optional
                .skipRows(3)
                .rowFilter(s -> s.matches("^\\d.*")) //we are only interested in rows that start with a number
                .build();

        List<Row> data = parser.parse(path)
                .collect(Collectors.toList());

        assertEquals(3, data.size());
    }
}