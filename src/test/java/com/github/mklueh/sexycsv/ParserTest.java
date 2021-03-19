package com.github.mklueh.sexycsv;

import com.github.mklueh.sexycsv.annotation.CSVColumn;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by m.kluehspies on 11/03/2021
 */
class ParserTest {

    @Data
    public static class MyEntity {

        @CSVColumn(value = "test", position = 0)
        public String test;

        @CSVColumn(value = "a_number", position = 1)
        public Integer number;

        @CSVColumn
        private String anotherString;
    }

    @Test
    void testParser2() {
        List<MyEntity> entities = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            MyEntity entity = new MyEntity();
            entity.setTest("s_" + i);
            entity.setNumber(i);
            entity.setAnotherString("bla");
            entities.add(entity);
        }

        SexyCSV.Creator<MyEntity> myEntityParser = SexyCSV.Creator.<MyEntity>builder()
                .withEntity(MyEntity.class)
                .build();

        String text = myEntityParser.toString(entities);
        System.out.println(text);
        assertNotNull(text);
    }
}