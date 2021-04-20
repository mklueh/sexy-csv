package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;
import lombok.Data;

/**
 * Sample entity used in tests.
 * Column headers are generated from annotations
 */
@Data
public class TestEntity {

    @CSVColumn(value = "id", position = 0)
    public String id;

    @CSVColumn(value = "name", position = 1)
    public String name;

    @CSVColumn(value = "age")
    private Integer age;

    @CSVColumn(value = "country")
    private String country;
}