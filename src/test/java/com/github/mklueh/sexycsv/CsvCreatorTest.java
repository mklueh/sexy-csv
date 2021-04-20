package com.github.mklueh.sexycsv;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CsvCreatorTest {

    @Test
    void testCreatorWithEntityHeader() {
        SexyCSV.Creator<TestEntity> creator = SexyCSV.Creator.<TestEntity>builder()
                .withEntity(TestEntity.class)
                .withTypeConverter(LocalDateTime.class, new LocalDateTimeConverter())
                .build();

        List<TestEntity> entities = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            TestEntity entity = new TestEntity();
            entity.setName("s_" + i);
            entity.setId(String.valueOf(i));
            entity.setAge(i);
            entity.setCountry("bla");
            entities.add(entity);
        }

        String text = creator.toString(entities.stream());
        System.out.println(text);
        assertNotNull(text);
    }
}