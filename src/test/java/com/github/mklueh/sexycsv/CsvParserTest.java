package com.github.mklueh.sexycsv;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {

    @Test
    void testParsingWithEntityHeaders() throws IOException {
        Path path = getCsv("sample-data-comma.csv");

        SexyCSV.Parser parser = SexyCSV.Parser
                .builder()
                .delimiter(",")
                .build();

        List<TestEntity> data = parser.parse(path, TestEntity.class).collect(Collectors.toList());

        data.forEach(row -> System.out.println(row.toString()));

        assertEquals(5, data.size());
    }

    @Test
//.header(Arrays.asList("id", "name", "age", "country")) set optional header
    void testParsingWithCustomTokenizer() throws IOException {
        Path path = getCsv("sample-data-comma.csv");

        SexyCSV.Parser parser = SexyCSV.Parser
                .builder()
                .hasHeaderRow(true) //auto-use of the given header
                .rowFilter(s -> s.matches("^\\d.*")) //we are only interested in rows that start with a number
                .tokenizer(s -> s.split(",")) //optional custom tokenizer
                .build();

        List<Row> data = parser.parse(path).collect(Collectors.toList());

        data.forEach(row -> System.out.println(row.getCellsByIndex()));

        for (int i = 0; i < data.size(); i++) {
            Row row = data.get(i);
            assertEquals(String.valueOf(i + 1), row.get("id"));
        }

        assertEquals(4, data.size());
    }

    @Test
    void testParsingCorruptedWithCustomTokenizerAndPreHeader() throws IOException {
        Path path = getCsv("sample-data-pre-header-comma-corrupted.csv");

        SexyCSV.Parser parser = SexyCSV.Parser
                .builder()
                .delimiter(",")
                .skipRows(3)
                .rowFilter(s -> s.matches("^\\d.*")) //we are only interested in rows that start with a number
                .build();

        List<TestEntity> data = parser.parse(path, TestEntity.class).collect(Collectors.toList());

        data.forEach(row -> System.out.println(row.toString()));

        assertEquals(3, data.size());
    }

    @Test
    void testParsingCorruptedWithTabAndPreHeader() throws IOException {
        Path path = getCsv("sample-data-pre-header-tab-corrupted.csv");

        SexyCSV.Parser parser = SexyCSV.Parser.builder()
                .delimiter("\t")
                .hasHeaderRow(true) //auto-use of the given header
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

    private Path getCsv(String file) {
        return Paths.get("src", "test", "resources", file);
    }

}