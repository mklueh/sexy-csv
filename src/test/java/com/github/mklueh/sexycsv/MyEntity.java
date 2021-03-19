package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;
import lombok.Data;

@Data
public class MyEntity {

    @CSVColumn(value = "id", position = 0)
    public String id;

    @CSVColumn(value = "name", position = 1)
    public String name;

    @CSVColumn(value = "age")
    private Integer age;

    @CSVColumn(value = "country")
    private String country;
}